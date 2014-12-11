package com.linda.cluster.redis.common.utils;

import org.apache.zookeeper.data.Stat;

import com.linda.cluster.redis.common.bean.CountBean;

public interface RedisSetNodeDataCallback {
	
	public Stat onNodeNotExist(String path,byte[] data,int version,CountBean count);
	
	public Stat onConnectionLoss(String path,byte[] data,int version,CountBean count);
	
	public Stat onZkException(String path,byte[] data,int version,CountBean count);
}
