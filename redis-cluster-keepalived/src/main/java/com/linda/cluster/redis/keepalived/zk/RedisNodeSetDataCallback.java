package com.linda.cluster.redis.keepalived.zk;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.utils.RedisSetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class RedisNodeSetDataCallback implements RedisSetNodeDataCallback{
	
	private ZooKeeper zooKeeper;
	
	public RedisNodeSetDataCallback(ZooKeeper zooKeeper){
		this.zooKeeper = zooKeeper;
	}
	
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
}
