package com.linda.cluster.redis.client;

import redis.clients.jedis.Jedis;


public interface RedisCallback {

	public void callback(Jedis jedis,RedisResult collector);
	
}
