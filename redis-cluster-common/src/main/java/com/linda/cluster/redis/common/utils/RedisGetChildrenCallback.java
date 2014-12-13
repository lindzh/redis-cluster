package com.linda.cluster.redis.common.utils;

import java.util.List;

import org.apache.zookeeper.Watcher;

import com.linda.cluster.redis.common.bean.CountBean;

public interface RedisGetChildrenCallback {

	public List<String> onConnectionLoss(String path,Watcher watcher,CountBean count);
	
	public List<String> onZkException(String path,Watcher watcher,CountBean count);
	
}
