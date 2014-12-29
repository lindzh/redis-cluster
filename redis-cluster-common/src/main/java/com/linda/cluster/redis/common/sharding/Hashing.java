package com.linda.cluster.redis.common.sharding;

public interface Hashing {
	
	public int hash(String key);

}
