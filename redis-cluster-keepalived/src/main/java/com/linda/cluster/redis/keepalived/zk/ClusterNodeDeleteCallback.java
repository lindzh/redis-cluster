package com.linda.cluster.redis.keepalived.zk;

import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.utils.RedisNodeDeleteCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class ClusterNodeDeleteCallback implements RedisNodeDeleteCallback{

	private ZooKeeper zooKeeper;
	
	public ClusterNodeDeleteCallback(ZooKeeper zooKeeper){
		this.zooKeeper = zooKeeper;
	}
	
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
}
