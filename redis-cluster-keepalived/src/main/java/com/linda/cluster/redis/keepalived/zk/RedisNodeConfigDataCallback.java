package com.linda.cluster.redis.keepalived.zk;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.utils.RedisGetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class RedisNodeConfigDataCallback implements RedisGetNodeDataCallback{

	private ZooKeeper zooKeeper;
	
	public RedisNodeConfigDataCallback(ZooKeeper zooKeeper){
		this.zooKeeper = zooKeeper;
	}
	
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
}
