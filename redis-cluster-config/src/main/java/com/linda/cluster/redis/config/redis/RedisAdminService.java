package com.linda.cluster.redis.config.redis;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.linda.cluster.redis.common.bean.ClusterStateBean;
import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.utils.RedisGetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisSetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;
import com.linda.cluster.redis.common.utils.ZkNodeCreateCallback;
import com.linda.cluster.redis.config.config.RedisProductDataBean;
import com.linda.cluster.redis.config.config.RedisZkConfig;

public class RedisAdminService {
	
	private ZooKeeper zookeeper;
	private RedisZkConfig zkConfig;
	private int retryTime = 5;
	
	private RedisGetNodeDataCallback getDataCallback = new RedisGetNodeDataCallback(){
		public RedisZkData onNodeNotExist(String path, byte[] data,Watcher watcher,CountBean count) {
			boolean node = RedisZookeeperUtils.zkCreateNode(zookeeper, path, data, CreateMode.PERSISTENT, count, createCallback);
			if(node){
				return RedisZookeeperUtils.zkGetNodeData(zookeeper, watcher, path, data, count, this);
			}
			return null;
		}

		public RedisZkData onConnectionLoss(String path, byte[] data,Watcher watcher,CountBean count) {
			if(count.getCount()>retryTime){
				return null;
			}
			return RedisZookeeperUtils.zkGetNodeData(zookeeper, watcher, path, data, count, this);
		}

		@Override
		public RedisZkData onZkException(String path, byte[] data,Watcher watcher,CountBean count) {
			return this.onConnectionLoss(path, data, watcher, count);
		}
	};
	
	private RedisSetNodeDataCallback setDataCallback = new RedisSetNodeDataCallback(){

		public Stat onNodeNotExist(String path, byte[] data, int version,CountBean count) {
			boolean createNode = RedisZookeeperUtils.zkCreateNode(zookeeper, path, data, CreateMode.PERSISTENT, count, createCallback);
			if(createNode){
				RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zookeeper, null, path, data, count, getDataCallback);
				if(zkData!=null){
					return zkData.getStat();
				}
			}
			return null;
		}

		@Override
		public Stat onConnectionLoss(String path, byte[] data, int version,CountBean count) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Stat onZkException(String path, byte[] data, int version,CountBean count) {
			return this.onConnectionLoss(path, data, version, count);
		}
		
	};
	
	private ZkNodeCreateCallback createCallback = new ZkNodeCreateCallback(){
		public boolean onNodeExist(String path, byte[] data,CountBean count) {
			RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zookeeper, null, path,data, count, getDataCallback);
			if(zkData!=null){
				Stat stat = RedisZookeeperUtils.zkSetNodeData(zookeeper, path, zkData.getVersion(), data, count, setDataCallback);
				return stat!=null;
			}
			return false;
		}
		public boolean onConnectionLoss(String path, byte[] data,CountBean count) {
			if(count.getCount()>retryTime){
				return false;
			}else{
				return RedisZookeeperUtils.zkCreateNode(zookeeper, path, data, CreateMode.PERSISTENT,count,this); 
			}
		}
		public boolean onZkException(String path, byte[] data,CountBean count) {
			return this.onConnectionLoss(path, data, count);
		}
	};
	
	/**
	 * 添加产品,在basepath下面创建节点name,data部分为databean
	 * @param name
	 * @param dataBean
	 * @return
	 */
	public boolean addOrUpdateProduct(String name,RedisProductDataBean dataBean){
		String path = RedisZookeeperUtils.genPath(zkConfig.getRedisBasePath(),name);
		byte[] data = RedisZookeeperUtils.getBytes(dataBean);
		CountBean count = new CountBean();
		return RedisZookeeperUtils.zkCreateNode(zookeeper, path, data, CreateMode.PERSISTENT,count,createCallback);
	}
	
	
	/**
	 * 添加集群，或者修改集群状态
	 * @param productName
	 * @param clustername
	 * @param state
	 * @return
	 */
	public boolean addOrUpdateCluster(String productName,String clustername,ClusterStateBean state){
		String path = RedisZookeeperUtils.genPath(zkConfig.getRedisBasePath(),productName,clustername);
		byte[] data = RedisZookeeperUtils.getBytes(state);
		CountBean count = new CountBean();
		return RedisZookeeperUtils.zkCreateNode(zookeeper, path, data, CreateMode.PERSISTENT,count,createCallback);
	}
	
	/**
	 * 添加节点，或者修改节点状态
	 * @param productName
	 * @param clustername
	 * @param nodename
	 * @param hostAndPort//节点状态
	 * @return
	 */
	public boolean addOrUpdateRedisNode(String productName,String clustername,String nodename,HostAndPort hostAndPort){
		String path = RedisZookeeperUtils.genPath(zkConfig.getRedisBasePath(),productName,clustername,nodename);
		byte[] data = RedisZookeeperUtils.getBytes(hostAndPort);
		CountBean count = new CountBean();
		return RedisZookeeperUtils.zkCreateNode(zookeeper, path, data, CreateMode.PERSISTENT,count,createCallback);
	}

}
