package com.linda.cluster.redis.keepalived.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;
import com.linda.cluster.redis.common.utils.ZkNodeCreateCallback;

public class MonitorNodeCreateCallback implements ZkNodeCreateCallback{
	
	private ZooKeeper zooKeeper;
	
	private CreateMode createMode;
	
	public MonitorNodeCreateCallback(ZooKeeper zooKeeper,CreateMode createMode){
		this.zooKeeper = zooKeeper;
		this.createMode = createMode;
	}
	
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
		return RedisZookeeperUtils.zkCreateNode(zooKeeper, path, data,createMode, count, this);
	}

	@Override
	public boolean onZkException(String path, byte[] data, CountBean count) {
		return this.onConnectionLoss(path, data, count);
	}
	
}
