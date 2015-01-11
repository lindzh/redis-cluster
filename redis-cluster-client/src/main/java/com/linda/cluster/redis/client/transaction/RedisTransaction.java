package com.linda.cluster.redis.client.transaction;

public interface RedisTransaction {
	
	public void startTransaction();
	
	public void commitTransaction();
	
	public void rollbackTransaction();

}
