package com.linda.cluster.redis.keepalived.zk;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.bean.ClusterStateBean;
import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.constant.RedisZkNodeConstant;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.common.utils.RedisGetChildrenCallback;
import com.linda.cluster.redis.common.utils.RedisGetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisNodeDeleteCallback;
import com.linda.cluster.redis.common.utils.RedisReplicationLinkUtils;
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
	
	private List<String> monitors = new CopyOnWriteArrayList<String>();
	
	//创建节点回调
	private ZkNodeCreateCallback monitorNodeCreateCallback = new ZkNodeCreateCallback(){
		@Override
		public boolean onNodeExist(String path, byte[] data, CountBean count) {
			//can't happen
			return false;
		}

		@Override
		public boolean onConnectionLoss(String path, byte[] data,CountBean count) {
			if(count.getCount()>5){
				return false;
			}
			return RedisZookeeperUtils.zkCreateNode(zooKeeper, path, data, CreateMode.EPHEMERAL_SEQUENTIAL, count, this);
		}

		@Override
		public boolean onZkException(String path, byte[] data, CountBean count) {
			return this.onConnectionLoss(path, data, count);
		}
	};
	
	//获取子节点回调
	private RedisGetChildrenCallback redisGetClusterNodesCallback = new RedisGetChildrenCallback(){
		@Override
		public List<String> onConnectionLoss(String path, Watcher watcher,CountBean count) {
			if(count.getCount()>5){
				return Collections.emptyList();
			}
			return RedisZookeeperUtils.zkGetChildren(zooKeeper, path, watcher, count, this);
		}

		@Override
		public List<String> onZkException(String path, Watcher watcher,CountBean count) {
			return this.onConnectionLoss(path, watcher, count);
		}
	};
	
	//节点删除回调
	private RedisNodeDeleteCallback redisNodeVoteDelCallback = new RedisNodeDeleteCallback(){
		@Override
		public boolean onNodeNotExist(String path, int version, CountBean count) {
			return true;
		}

		@Override
		public boolean onConnectionLoss(String path, int version,CountBean count) {
			int cc = count.getCount();
			if(cc>5){
				return false;
			}
			return RedisZookeeperUtils.zkDeleteNode(zooKeeper, version, path, count, this);
		}

		@Override
		public boolean onZkException(String path, int version, CountBean count) {
			return this.onConnectionLoss(path, version, count);
		}
		
	};
	
	//获取节点数据回调
	private RedisGetNodeDataCallback redisGetNodeDataCallback = new RedisGetNodeDataCallback(){
		@Override
		public RedisZkData onNodeNotExist(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}

		@Override
		public RedisZkData onConnectionLoss(String path, byte[] data,Watcher watcher, CountBean count) {
			if(count.getCount()>5){
				return null;
			}
			return RedisZookeeperUtils.zkGetNodeData(zooKeeper, watcher, path, data, count, this);
		}

		@Override
		public RedisZkData onZkException(String path, byte[] data,Watcher watcher, CountBean count) {
			return this.onConnectionLoss(path, data, watcher, count);
		}
	};
	
	//设置节点数据回调
	private RedisSetNodeDataCallback redisSetNodeDataCallback = new RedisSetNodeDataCallback(){

		@Override
		public Stat onNodeNotExist(String path, byte[] data, int version,CountBean count) {
			return null;
		}

		@Override
		public Stat onConnectionLoss(String path, byte[] data, int version,CountBean count) {
			if(count.getCount()>5){
				return null;
			}else{
				return RedisZookeeperUtils.zkSetNodeData(zooKeeper, path, version, data, count, this);
			}
		}

		@Override
		public Stat onZkException(String path, byte[] data, int version,CountBean count) {
			return this.onConnectionLoss(path, data, version, count);
		}
	};
	
	private EventType filterEvent(WatchedEvent event){
		EventType type = event.getType();
		KeeperState state = event.getState();
		if(state==KeeperState.SyncConnected){
			return type;
		}else if(state==KeeperState.Disconnected){
			this.doWithZkNotConnected();
		}
		return null;
	}
	
	private void doWithZkNotConnected(){
		logger.error("zk service not connected");
	}
	
	private void redisNodeDeleted(String node){
		HostAndPort hostAndPort = monitorNodes.get(node);
		if(hostAndPort!=null){
			SimpleRedisAliveNode alive = redisNodePingService.getByHostAndPort(hostAndPort);
			if(alive!=null){
				logger.error("redis node delete start to shutdown alive");
				alive.close();
				redisNodePingService.remodeNode(alive);
			}
		}
	}
	
	private void redisClusterDeleted(String cluster){
		if(cluster!=null&&cluster.endsWith(this.clusterName)){
			logger.error("redis cluster deleted start to shutdown");
			this.shutdown();
		}
	}
	
	//product->cluster->node->votes->4343 votes路径监控
	private Watcher redisVotesWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			String path = event.getPath();
			logger.info("watcher vote:path:"+path);
			String redisNode = RedisZookeeperUtils.getClusterRedisNodeName(path);
			EventType eventType = RedisZkClusterAliveService.this.filterEvent(event);
			if(eventType!=null){
				if(eventType == EventType.NodeChildrenChanged){
					RedisZkClusterAliveService.this.getRedisNodeVotes(redisNode);
				}else if(eventType == EventType.NodeDeleted){
					RedisZkClusterAliveService.this.redisNodeDeleted(redisNode);
				}
			}
		}
	};
	
	//product->cluster data监控,集群master节点改变
	private Watcher redisClusterDataWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			String path = event.getPath();
			String cluster = RedisZookeeperUtils.getClusterName(path);
			logger.info("watcher cluster:path:"+path);
			EventType eventType = RedisZkClusterAliveService.this.filterEvent(event);
			if(eventType!=null){
				if(eventType==EventType.NodeDataChanged){//master改变
					RedisZkClusterAliveService.this.getClusterNodes();
					RedisZkClusterAliveService.this.checkAndChooseMaster();
				}else if(eventType==EventType.NodeDeleted){//集群删除
					RedisZkClusterAliveService.this.redisClusterDeleted(cluster);
				}else if(eventType==EventType.NodeChildrenChanged){//集群节点删除或者掉线
					RedisZkClusterAliveService.this.getClusterNodes();
					RedisZkClusterAliveService.this.checkAndChooseMaster();
				}
			}
		}
	};
	
	private Watcher redisNodeDataWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			String path = event.getPath();
			String nodeName = RedisZookeeperUtils.getClusterRedisNodeName(path);
			logger.info("watcher node:path:"+path);
			EventType eventType = RedisZkClusterAliveService.this.filterEvent(event);
			if(eventType!=null){
				if(eventType==EventType.NodeDataChanged){
					RedisZkClusterAliveService.this.getRedisNodeData(nodeName);
				}else if(eventType==EventType.NodeDeleted){
					RedisZkClusterAliveService.this.redisNodeDeleted(nodeName);
				}
			}
		}
	};
	
	private Watcher redisClusterNodesWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			String path = event.getPath();
			String cluster = RedisZookeeperUtils.getClusterName(path);
			logger.info("watcher cluster:path:"+path);
			EventType eventType = RedisZkClusterAliveService.this.filterEvent(event);
			if(eventType!=null){
				if(eventType==EventType.NodeChildrenChanged){
					RedisZkClusterAliveService.this.getClusterNodes();
					RedisZkClusterAliveService.this.checkAndChooseMaster();
				}else if(eventType==EventType.NodeDeleted){
					RedisZkClusterAliveService.this.redisClusterDeleted(cluster);
				}
			}
		}
	};
	
	private Watcher redisClusterMonitorMasterWatcher = new Watcher(){
		public void process(WatchedEvent event) {
			String path = event.getPath();
			String cluster = RedisZookeeperUtils.getClusterName(path);
			logger.info("watcher cluster:path:"+path);
			EventType eventType = RedisZkClusterAliveService.this.filterEvent(event);
			if(eventType!=null){
				RedisZkClusterAliveService.this.checkClusterMonitorMaster();
			}
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
			this.checkClusterMonitorMaster();
		}else{
			logger.error("can't get monitors after create on cluster "+productName+"/"+clusterName);
		}
	}
	
	private void checkClusterMonitorMaster(){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,monitorPathName,RedisZkNodeConstant.REDIS_CLUSTER_MONITORS_MONITOR_NODE);
		CountBean monitorMasterCountBean = new CountBean();
		monitors.clear();
		List<String> refreshMonitors = RedisZookeeperUtils.zkGetChildren(zooKeeper, path, redisClusterMonitorMasterWatcher, monitorMasterCountBean, redisGetClusterNodesCallback);
		monitors.addAll(refreshMonitors);
		this.checkMonitorMaster(monitors);
	}
	
	private ClusterStateBean getClusterStateAndMaster(){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName);
		byte[] data = RedisZkNodeConstant.REDIS_NODE_NULL_DATA.getBytes();
		CountBean bean = new CountBean();
		RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zooKeeper,redisClusterDataWatcher, path, data, bean, redisGetNodeDataCallback);
		if(zkData!=null&&zkData.getDataLength()>0){
			String conf = new String(zkData.getData());
			if(StringUtils.isNotBlank(conf)){
				ClusterStateBean stateBean = JSONUtils.fromJson(conf, ClusterStateBean.class);
				stateBean.setStat(zkData.getStat());
				return stateBean;
			}
		}
		return null;
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
			if(redisNode.equals(monitorPathName)){
				continue;
			}
			boolean hasNode = this.getRedisNodeData(redisNode);
			if(hasNode){
				this.getRedisNodeVotes(redisNode);
			}
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
			hostAndPort.setStat(zkData.getStat());
			if(hostAndPort!=null){
				return this.monitorRedisNode(hostAndPort);
			}
		}
		return false;
	}
	
	private boolean monitorRedisNode(HostAndPort hostAndPort){
		try{
			HostAndPort oldHostAndPort = monitorNodes.get(hostAndPort.getName());
			if(oldHostAndPort==null||redisNodePingService.getByHostAndPort(oldHostAndPort)==null){
				SimpleRedisAliveNode redisAliveNode = new SimpleRedisAliveNode();
				redisAliveNode.setRedisHost(hostAndPort);
				redisAliveNode.addRedisListener(this);
				redisNodePingService.addRedisNode(redisAliveNode);
				redisAliveNode.connect();
			}else{
				oldHostAndPort.copyFileds(hostAndPort);
				redisNodePingService.getByHostAndPort(oldHostAndPort).connect();
			}
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
		this.shutdownNodeIfVote(redisNode, votes);
	}
	
	private void shutdownNodeIfVote(String redisNode,List<String> votes){
		//投票超过一定数量才会
		if(votes.size()>monitors.size()/2){
			if(isClusterMonitorMaster.get()){
				HostAndPort hostAndPort = monitorNodes.get(redisNode);
				hostAndPort.setAlive(false);
				byte[] data = RedisZookeeperUtils.getBytes(hostAndPort);
				String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,redisNode);
				CountBean countBean = new CountBean();
				Stat stat = RedisZookeeperUtils.zkSetNodeData(zooKeeper, path, hostAndPort.getStat().getVersion(), data, countBean, redisSetNodeDataCallback);
				if(stat!=null){
					hostAndPort.setStat(stat);
					this.checkAndChooseMaster();
				}
			}
		}
	}
	
	private void checkAndChooseMaster(){
		if(this.isClusterMonitorMaster.get()){
			ClusterStateBean clusterState = this.getClusterStateAndMaster();
			Map<String, HostAndPort> copy = RedisReplicationLinkUtils.copy(monitorNodes);
			HostAndPort master = RedisReplicationLinkUtils.linkRedisNodes(monitorNodes, clusterState);
			List<HostAndPort> changed = RedisReplicationLinkUtils.getChanged(copy, monitorNodes);
			this.changeRedisNodeData(changed);
			if(master!=null){
				if(clusterState==null){
					clusterState = new ClusterStateBean();
					clusterState.setAlive(false);
					clusterState.setMaster("");
					clusterState.setStat(new Stat());
				}
				if(!clusterState.isAlive()||!clusterState.getMaster().equals(master.getName())){
					clusterState.setAlive(true);
					clusterState.setMaster(master.getName());
					byte[] data = RedisZookeeperUtils.getBytes(clusterState);
					String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName);
					RedisZookeeperUtils.zkSetNodeData(zooKeeper, path, clusterState.getStat().getVersion(), data, new CountBean(), redisSetNodeDataCallback);
				}
			}
		}
	}
	
	private void changeRedisNodeData(List<HostAndPort> changes){
		for(HostAndPort host:changes){
			SimpleRedisAliveNode redisAlive = redisNodePingService.getByHostAndPort(host);
			HostAndPort master = monitorNodes.get(host.getMaster());
			if(redisAlive!=null&&master!=null){
				boolean slaveOf = redisAlive.slaveOf(master);
				if(slaveOf){
					String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,host.getName());
					byte[] data = RedisZookeeperUtils.getBytes(host);
					RedisZookeeperUtils.zkSetNodeData(zooKeeper, path, 1, data, new CountBean(), redisSetNodeDataCallback);
				}
			}
		}
	}
	
	/**
	 * redis集群高可用启动
	 * 先加入监控节点集合
	 * 然后监控节点，更新监控状态，更新master slave关系
	 */
	@Override
	public void startup() {
		//启动监控节点ping thread
		redisNodePingService.startup();
		//创建监控节点/base/product/cluseter/monitors/monitor-0
		this.createMonitorNode();
		//获取redis节点
		this.getClusterNodes();
		//更新 master slave节点
		this.checkAndChooseMaster();
	}
	
	@Override
	public void shutdown() {
		this.redisNodePingService.shutdown();
		this.monitorNodes.clear();
		this.monitors.clear();
	}
	
	private void voteReidsNodeDown(HostAndPort hostAndPort){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,hostAndPort.getName(),votePathName,String.valueOf(this.monitorId));
		byte[] data = RedisZkNodeConstant.REDIS_NODE_NULL_DATA.getBytes();
		boolean createNode = RedisZookeeperUtils.zkCreateNode(zooKeeper, path, data, CreateMode.EPHEMERAL, new CountBean(), monitorNodeCreateCallback);
	}
	
	private void voteRedisNodeUp(HostAndPort hostAndPort){
		String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,hostAndPort.getName(),votePathName,String.valueOf(this.monitorId));
		boolean deleteNode = RedisZookeeperUtils.zkDeleteNode(zooKeeper, 0, path, new CountBean(), redisNodeVoteDelCallback);
	}
	
	/**
	 * listener事件，连接成功会响应该事件，方便添加到ping中，同时更改状态
	 * @param redis
	 */
	@Override
	public void onConnected(RedisAliveBase redis) {
		HostAndPort hostAndPort = redis.getRedisHost();
		monitorNodes.put(hostAndPort.getName(), hostAndPort);
		boolean alive = hostAndPort.isAlive();
		if(!alive){
			hostAndPort.setAlive(true);
			String path = RedisZookeeperUtils.genPath(zkBase,productName,clusterName,hostAndPort.getName());
			byte[] data = RedisZookeeperUtils.getBytes(hostAndPort);
			CountBean countBean = new CountBean();
			//版本错误要求重新设置
			Stat stat = RedisZookeeperUtils.zkSetNodeData(zooKeeper, path, hostAndPort.getStat().getVersion(), data, countBean, redisSetNodeDataCallback);
			if(stat!=null){
				this.checkAndChooseMaster();
			}
		}
		this.voteRedisNodeUp(hostAndPort);
	}

	@Override
	public void onClose(RedisAliveBase redis) {
		HostAndPort host = redis.getRedisHost();
		this.voteReidsNodeDown(host);
	}

	@Override
	public void onException(RedisAliveBase redis, Exception e) {
		HostAndPort host = redis.getRedisHost();
		this.voteReidsNodeDown(host);
	}

	@Override
	public void onInfo(RedisAliveBase redis, String info) {
		//do nothing
	}
}
