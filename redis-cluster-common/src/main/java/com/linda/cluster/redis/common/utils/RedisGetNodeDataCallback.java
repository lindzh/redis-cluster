package com.linda.cluster.redis.common.utils;

import org.apache.zookeeper.Watcher;

import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.RedisZkData;

public interface RedisGetNodeDataCallback {
	
	public RedisZkData onNodeNotExist(String path,byte[] data,Watcher watcher,CountBean count);
	
	public RedisZkData onConnectionLoss(String path,byte[] data,Watcher watcher,CountBean count);
	
	public RedisZkData onZkException(String path,byte[] data,Watcher watcher,CountBean count);
}
