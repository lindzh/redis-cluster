package com.linda.cluster.redis.keepalived.zk;

public interface ZkPathCreateCallback {

	public void callback(String path,byte[] data);
	
}
