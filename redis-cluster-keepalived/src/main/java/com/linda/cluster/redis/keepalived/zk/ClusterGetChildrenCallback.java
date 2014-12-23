package com.linda.cluster.redis.keepalived.zk;

import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.utils.RedisGetChildrenCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class ClusterGetChildrenCallback implements RedisGetChildrenCallback{
	
	private ZooKeeper zooKeeper;
	
	public ClusterGetChildrenCallback(ZooKeeper zooKeeper){
		this.zooKeeper = zooKeeper;
	}
	
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
}
