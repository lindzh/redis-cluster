package com.linda.cluster.redis.keepalived.zk;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.constant.RedisZkNodeConstant;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.common.utils.RedisGetChildrenCallback;
import com.linda.cluster.redis.common.utils.RedisGetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisSetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;
import com.linda.cluster.redis.common.utils.ZkNodeCreateCallback;
import com.linda.cluster.redis.keepalived.redis.MultiRedisAlivedPingService;
import com.linda.cluster.redis.keepalived.redis.RedisAliveBase;
import com.linda.cluster.redis.keepalived.redis.RedisAlivedListener;
import com.linda.cluster.redis.keepalived.redis.SimpleRedisAliveNode;


public class RedisZkClusterAliveService implements Service,RedisAlivedListener{
	
	private ZooKeeper zooKeeper;
	
	private String zkBase;
	
	private String productName;

	private String clusterName;
	
	private int monitorId;
	
	private String monitorPathName = RedisZkNodeConstant.REDIS_PRODUCT_CUSTER_MONITOR_NODE;
	
	private String votePathName = RedisZkNodeConstant.REDIS_PORDUCT_CLUSTER_NODE_VOTE_NODE;
	
	private MultiRedisAlivedPingService redisNodePingService = new MultiRedisAlivedPingService();
	
	private ConcurrentHashMap<String, HostAndPort> monitorNodes = new ConcurrentHashMap<String, HostAndPort>();
	
	private AtomicBoolean isClusterMonitorMaster = new AtomicBoolean(false);
	
	private Logger logger = Logger.getLogger(RedisZkClusterAliveService.class);
	
	private ZkNodeCreateCallback monitorNodeCreateCallback = new ZkNodeCreateCallback(){
		@Override
		public boolean onNodeExist(String path, byte[] data, CountBean count) {
			return false;
		}

		@Override
		public boolean onConnectionLoss(String path, byte[] data,CountBean count) {
			return false;
		}

		@Override
		public boolean onZkException(String path, byte[] data, CountBean count) {
			return false;
		}
	};
	
	private RedisGetChildrenCallback redisGetClusterNodesCallback = new RedisGetChildrenCallback(){
		@Override
		public List<String> onConnectionLoss(String path, Watcher watcher,CountBean count) {
			return null;
		}

		@Override
		public List<String> onZkException(String path, Watcher watcher,CountBean count) {
			return null;
		}
	};
	
	private RedisGetNodeDataCallback redisGetNodeDataCallback = new RedisGetNodeDataCallback(){
		@Override
		public RedisZkData onNodeNotExist(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}

		@Override
		public RedisZkData onConnectionLoss(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}

		@Override
		public RedisZkData onZkException(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}
	};
	
	private RedisSetNodeDataCallback redisSetNodeDataCallback = new RedisSetNodeDataCallback(){

		@Override
		public Stat onNodeNotExist(String path, byte[] data, int version,CountBean count) {
			return null;
		}

		@Override
		public Stat onConnectionLoss(String path, byte[] data, int version,CountBean count) {
			return null;
		}

		@Override
		public Stat onZkException(String path, byte[] data, int version,CountBean count) {
			return null;
		}
		
	};
	
	private Watcher redisVotesWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			
		}
	};
	
	private Watcher redisNodeDataWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			
		}
	};
	
	private Watcher redisClusterNodesWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			
		}
	};
	
	private Watcher redisClusterMonitorMasterWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			
		}
	};
	
	public RedisZkClusterAliveService(ZooKeeper zooKeeper,String zkBase,String productName,String clusterName){
		this(zooKeeper, zkBase, productName, clusterName, 5000);
	}
	
	public RedisZkClusterAliveService(ZooKeeper zooKeeper,String zkBase,String productName,String clusterName,int pingInterval){
		this.zooKeeper = zooKeeper;
		this.zkBase = zkBase;
		this.productName = productName;
		this.clusterName = clusterName;
		Random random = new Random();
		monitorId = Math.abs(random.nextInt(100000000));
		redisNodePingService.setInterval(pingInterval);
	}
	
	private void createMonitorNode(){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,monitorPathName,RedisZkNodeConstant.REDIS_CLUSTER_MONITORS_MONITOR_NODE);
		byte[] data = String.valueOf(monitorId).getBytes();
		CountBean countBean = new CountBean();
		boolean createMonitorResult = RedisZookeeperUtils.zkCreateNode(zooKeeper, path, data, CreateMode.EPHEMERAL_SEQUENTIAL, countBean, monitorNodeCreateCallback);
		if(createMonitorResult){
			CountBean monitorMasterCountBean = new CountBean();
			List<String> monitors = RedisZookeeperUtils.zkGetChildren(zooKeeper, path, redisClusterMonitorMasterWatcher, monitorMasterCountBean, redisGetClusterNodesCallback);
			this.checkMonitorMaster(monitors);
		}else{
			logger.error("can't get monitors after create on cluster "+productName+"/"+clusterName);
		}
	}
	
	private void checkMonitorMaster(List<String> monitors){
		String master = RedisZookeeperUtils.getMaster(monitors, RedisZkNodeConstant.REDIS_CLUSTER_MONITORS_MONITOR_NODE);
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,monitorPathName,master);
		byte[] data = RedisZkNodeConstant.REDIS_NODE_NULL_DATA.getBytes();
		CountBean countBean = new CountBean();
		RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zooKeeper, null, path, data, countBean, redisGetNodeDataCallback);
		if(zkData!=null&&zkData.getData()!=null){
			int id = Integer.parseInt(new String(zkData.getData()));
			if(monitorId==id){
				this.isClusterMonitorMaster.set(true);
			}
		}
	}
	
	private void getClusterNodes(){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName);
		CountBean countBean = new CountBean();
		List<String> redisNodes = RedisZookeeperUtils.zkGetChildren(zooKeeper, path, redisClusterNodesWatcher, countBean, redisGetClusterNodesCallback);
		for(String redisNode:redisNodes){
			boolean hasNode = this.getRedisNodeData(redisNode);
			if(hasNode){
				this.getRedisNodeVotes(redisNode);
			}
		}
		this.checkAndChooseMaster();
	}
	
	private void checkAndChooseMaster(){
		if(this.isClusterMonitorMaster.get()){
			//calculate master and set master
			
		}
	}
	
	private boolean getRedisNodeData(String redisNode){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,redisNode);
		CountBean countBean = new CountBean();
		byte[] data = RedisZkNodeConstant.REDIS_NODE_NULL_DATA.getBytes();
		RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zooKeeper, redisNodeDataWatcher, path, data, countBean, redisGetNodeDataCallback);
		if(zkData!=null&&zkData.getData()!=null&&zkData.getData().length>5){
			String jsonHostAndPortData = new String(zkData.getData());
			HostAndPort hostAndPort = JSONUtils.fromJson(jsonHostAndPortData, HostAndPort.class);
			hostAndPort.setName(redisNode);
			if(hostAndPort!=null){
				return this.monitorRedisNode(hostAndPort);
			}
		}
		return false;
	}
	
	private boolean monitorRedisNode(HostAndPort hostAndPort){
		try{
			SimpleRedisAliveNode redisAliveNode = new SimpleRedisAliveNode();
			redisAliveNode.setRedisHost(hostAndPort);
			redisAliveNode.addRedisListener(this);
			redisAliveNode.connect();
			return true;
		}catch(Exception e){
			logger.error("start monitor redis node error:"+e.getMessage()+" node:"+hostAndPort.getHost()+":"+hostAndPort.getPort());
		}
		return false;
	}
	
	private void getRedisNodeVotes(String redisNode){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,redisNode,votePathName);
		CountBean countBean = new CountBean();
		List<String> votes = RedisZookeeperUtils.zkGetChildren(zooKeeper, path, redisVotesWatcher, countBean, redisGetClusterNodesCallback);
		//TODO
	}
	
	@Override
	public void startup() {
		redisNodePingService.startup();
		this.createMonitorNode();
		this.getClusterNodes();
	}

	@Override
	public void shutdown() {
		
	}
	
	/**
	 * listener事件，连接成功会响应该事件，方便添加到ping中，同时更改状态
	 * @param redis
	 */
	@Override
	public void onConnected(RedisAliveBase redis) {
		redisNodePingService.addRedisNode((SimpleRedisAliveNode)redis);
		HostAndPort hostAndPort = redis.getRedisHost();
		monitorNodes.put(hostAndPort.getName(), hostAndPort);
		boolean alive = hostAndPort.isAlive();
		if(!alive){
			hostAndPort.setAlive(true);
			String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,hostAndPort.getName());
			byte[] data = RedisZookeeperUtils.getBytes(hostAndPort);
			CountBean countBean = new CountBean();
			//版本错误要求重新设置
			Stat stat = RedisZookeeperUtils.zkSetNodeData(zooKeeper, path, 1, data, countBean, redisSetNodeDataCallback);
			//这个地方还不能选举master
		}
	}

	@Override
	public void onClose(RedisAliveBase redis) {
		
	}

	@Override
	public void onException(RedisAliveBase redis, Exception e) {
		
	}

	@Override
	public void onInfo(RedisAliveBase redis, String info) {
		
	}
}
