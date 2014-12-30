package com.linda.cluster.redis.client.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import redis.clients.jedis.JedisPoolConfig;

import com.linda.cluster.redis.client.utils.RedisUtils;
import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.bean.ClusterStateBean;
import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.bean.RedisProductDataBean;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.sharding.Sharding;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.common.utils.RedisGetChildrenCallback;
import com.linda.cluster.redis.common.utils.RedisGetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class ClusterRedisConnectionHandler extends AbstractClusterConnectionHandler implements Watcher,Service{
	
	//zk service list
	private List<HostAndPort> zkhosts;
	//zk base path
	private String redisBasePath;
	//product config
	private String product;
	//product password
	private String password;
	
	private int sessionTimeout = 10000;
	
	private ZooKeeper zooKeeper;
	
	private int retrytime = 5;
	
	private ConcurrentHashMap<String, Sharding> productShardingConf = new ConcurrentHashMap<String, Sharding>();
	
	private Logger logger = Logger.getLogger(ClusterRedisConnectionHandler.class);
	
	private Object zkMutex = new Object();
	
	public ClusterRedisConnectionHandler(JedisPoolConfig poolConfig,List<HostAndPort> zkhosts,String product){
		this(poolConfig,zkhosts,product,null,null);
	}
	
	public ClusterRedisConnectionHandler(JedisPoolConfig poolConfig,List<HostAndPort> zkhosts,String product,String password){
		this(poolConfig,zkhosts,product,password,null);
	}
	
	public ClusterRedisConnectionHandler(JedisPoolConfig poolConfig,List<HostAndPort> zkhosts,String product,String password,String basepath){
		super(poolConfig);
		this.zkhosts = zkhosts;
		this.product = product;
		this.password = password;
		this.redisBasePath = basepath;
		if(redisBasePath==null){
			redisBasePath = "/";
		}
		this.init();
	}
	
	private void init(){
		String zkServers = RedisZookeeperUtils.toString(zkhosts);
		logger.info("start zk connect:"+zkServers);
		try {
			zooKeeper = new ZooKeeper(zkServers,sessionTimeout,this);
			synchronized(zkMutex){
				zkMutex.wait();
			}
			logger.info("zk connect finish");
			this.startup();
		} catch (IOException e) {
			throw new IllegalArgumentException("redis zk service error:"+e.getMessage());
		} catch (InterruptedException e) {
			throw new IllegalArgumentException("redis zk service stop by interrupt:"+e.getMessage());
		}
		logger.info("load configs success");
	}
	
	public void close(){
		this.shutdown();
	}

	@Override
	public void process(WatchedEvent event) {
		KeeperState state = event.getState();
		if(state==KeeperState.SyncConnected){
			synchronized(zkMutex){
				zkMutex.notifyAll();
			}
		}
	}
	
	private Watcher shardingWatcher = new Watcher(){
		@Override
		public void process(WatchedEvent event) {
			EventType type = event.getType();
			if(type==EventType.NodeDataChanged){
				ClusterRedisConnectionHandler.this.getShardingData();
			}else if(type==EventType.NodeDeleted){
				logger.error("product has ben delete error error!!!!!!!!!");
			}
		}
	};
	
	private Watcher productClustersWatcher = new Watcher(){
		@Override
		public void process(WatchedEvent event) {
			EventType type = event.getType();
			if(type==EventType.NodeChildrenChanged){
				ClusterRedisConnectionHandler.this.getClusters();
			}else if(type==EventType.NodeDeleted){
				logger.error("product has ben delete error error!!!!!!!!!");
			}
		}
	};
	
	private Watcher clusterDataWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			String path = event.getPath();
			String cluster = RedisZookeeperUtils.getClusterName(path);
			EventType type = event.getType();
			if(type==EventType.NodeDataChanged){
				ClusterRedisConnectionHandler.this.getClusterData(cluster);
			}else if(type==EventType.NodeDeleted){
				logger.error(" cluster has been deleted");
				ArrayList<String> clusters = new ArrayList<String>();
				clusters.add(cluster);
				ClusterRedisConnectionHandler.this.handleDeleteClusters(clusters);
			}
		}
	};
	
	private Watcher nodeDataWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			// node data change do nothing,the node will change
		}
	};
	
	private RedisGetNodeDataCallback dataCallback = new RedisGetNodeDataCallback(){
		public RedisZkData onNodeNotExist(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}

		public RedisZkData onConnectionLoss(String path, byte[] data,Watcher watcher, CountBean count) {
			if(count.getCount()<retrytime){
				return RedisZookeeperUtils.zkGetNodeData(zooKeeper, watcher, path, data, count, this);
			}else{
				return null;
			}
		}

		public RedisZkData onZkException(String path, byte[] data,Watcher watcher, CountBean count) {
			return this.onConnectionLoss(path, data, watcher, count);
		}
	};
	
	private RedisGetChildrenCallback productClustersCallback = new RedisGetChildrenCallback(){
		@Override
		public List<String> onConnectionLoss(String path, Watcher watcher,CountBean count) {
			if(count.getCount()<retrytime){
				return RedisZookeeperUtils.zkGetChildren(zooKeeper, path, watcher, count, this);
			}else{
				return Collections.emptyList();
			}
		}
		@Override
		public List<String> onZkException(String path, Watcher watcher,CountBean count) {
			return this.onConnectionLoss(path, watcher, count);
		}
	};
	
	/**
	 * 集群列表发生变化，一次遍历找出增加的集群，删除的集群，分别修改处理
	 */
	private void getProductClusters(){
		String path = RedisZookeeperUtils.genPath(this.redisBasePath,product);
		List<String> newClusters = RedisZookeeperUtils.zkGetChildren(zooKeeper, path, productClustersWatcher, new CountBean(), productClustersCallback);
		logger.info("load clusters:"+JSONUtils.toJson(newClusters));
		Collection<String> oldClusters = this.getClusters();
		List<String> deletes = RedisUtils.getAListNotInBList(oldClusters, newClusters);
		List<String> adds = RedisUtils.getAListNotInBList(newClusters, oldClusters);
		this.handleAddClusters(adds);
		this.handleDeleteClusters(deletes);
	}
	
	private void handleDeleteClusters(List<String> deletes){
		for(String cluster:deletes){
			this.removeCluster(cluster);
		}
	}
	
	
	private void handleAddClusters(List<String> adds){
		for(String cluster:adds){
			this.getClusterData(cluster);
		}
	}
	
	/**
	 * 获取单个集群数据 先获取集群的data，data中存储master，master节点的ip存储在master节点的data中
	 * @param cluster
	 */
	private void getClusterData(String cluster){
		String path = RedisZookeeperUtils.genPath(this.redisBasePath,product,cluster);
		RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zooKeeper, clusterDataWatcher, path, null, new CountBean(), dataCallback);
		if(zkData!=null&&zkData.getData()!=null){
			String clusterStateBeanConf = new String(zkData.getData());
			logger.info("load cluster:"+cluster+" data:"+clusterStateBeanConf);
			ClusterStateBean stateBean = JSONUtils.fromJson(clusterStateBeanConf, ClusterStateBean.class);
			if(stateBean!=null&&stateBean.isAlive()){
				String master = stateBean.getMaster();
				if(master!=null){
					String nodePath = RedisZookeeperUtils.genPath(this.redisBasePath,product,cluster,master);
					RedisZkData redisNodeData = RedisZookeeperUtils.zkGetNodeData(zooKeeper, nodeDataWatcher, nodePath, null, new CountBean(), dataCallback);
					if(redisNodeData!=null&&redisNodeData.getData()!=null){
						String hostAndPortJson = new String(redisNodeData.getData());
						HostAndPort hostAndPort = JSONUtils.fromJson(hostAndPortJson, HostAndPort.class);
						if(hostAndPort!=null&&hostAndPort.isAlive()){
							logger.info("load master node cluster:"+cluster+" node:"+hostAndPortJson);
							this.changeClusterHostAndPort(cluster, hostAndPort);
						}else{
							logger.error("cluster master node data state error! cluster:"+cluster+" node:"+master+" data:"+hostAndPortJson);
						}
					}else{
						logger.error("cluster master node data null! cluster:"+cluster+"  node:"+master);
					}
				}else{
					logger.error("cluster data state master null! cluster:"+cluster);
				}
			}else{
				logger.error("cluster data state error! cluster"+cluster+" state:"+clusterStateBeanConf);
			}
		}else{
			logger.error("get cluster data error! cluster:"+cluster);
		}
	}
	
	/**
	 * sharding数据存储在product的data部分
	 * @param sharding
	 */
	private void getShardingData(){
		String path = RedisZookeeperUtils.genPath(this.redisBasePath,product);
		RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zooKeeper, shardingWatcher, path, null, new CountBean(), dataCallback);
		if(zkData!=null&&zkData.getData()!=null){
			String shardingJson = new String(zkData.getData());
			RedisProductDataBean sharding = JSONUtils.fromJson(shardingJson, RedisProductDataBean.class);
			this.handleShardingChange(sharding);
		}else{
			logger.error("get sharding data error");
		}
	}
	
	/**
	 * sharding发生变化，更新即可
	 * @param sharding
	 */
	private void handleShardingChange(RedisProductDataBean sharding){
		List<Sharding> shardings = sharding.getSharding();
		for(Sharding shard:shardings){
			String node = shard.getNode();
			Sharding oldSharding = productShardingConf.get(node);
			if(!RedisUtils.shardingEqual(oldSharding, shard)){
				this.changeSharding(shard);
			}
		}
	}
	
	/**
	 * 数据hash分片变了，改变节点即可
	 * @param shard
	 */
	private void changeSharding(Sharding shard){
		for(int i=shard.getFrom();i<=shard.getTo();i++){
			this.changeSharding(i, shard.getNode());
		}
	}

	/**
	 * 列表更新时先更新集群，再更新sharding
	 */
	@Override
	public void startup() {
		logger.info("start to load product clusters");
		//获取集群列表
		this.getProductClusters();
		//获取映射表
		this.getShardingData();
		
	}

	@Override
	public void shutdown() {
		if(zooKeeper!=null){
			try {
				zooKeeper.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.close();
	}
}
