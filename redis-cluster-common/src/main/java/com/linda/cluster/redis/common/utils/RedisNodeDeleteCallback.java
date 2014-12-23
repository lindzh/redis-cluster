package com.linda.cluster.redis.common.utils;

import com.linda.cluster.redis.common.bean.CountBean;

public interface RedisNodeDeleteCallback {
	
	public boolean onNodeNotExist(String path,int version,CountBean count);
	
	public boolean onConnectionLoss(String path,int version,CountBean count);
	
	public boolean onZkException(String path,int version,CountBean count);

}
