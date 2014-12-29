package com.linda.cluster.redis.common.sharding;

public class SimpleHashing implements Hashing{

	@Override
	public int hash(String key) {
		int code = key.hashCode();
		return code%256;
	}

}
