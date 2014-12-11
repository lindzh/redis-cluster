package com.linda.cluster.redis.common.utils;

import com.linda.cluster.redis.common.bean.CountBean;

public interface ZkNodeCreateCallback {
	
	public boolean onNodeExist(String path,byte[] data,CountBean count);
	
	public boolean onConnectionLoss(String path,byte[] data,CountBean count);
	
	public boolean onZkException(String path,byte[] data,CountBean count);

}
