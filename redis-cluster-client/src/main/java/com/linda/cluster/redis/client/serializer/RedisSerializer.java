package com.linda.cluster.redis.client.serializer;

public interface RedisSerializer {
	
	public byte[] serializeKey(String key);
	
	public byte[] serializeValue(Object value);
	
}
