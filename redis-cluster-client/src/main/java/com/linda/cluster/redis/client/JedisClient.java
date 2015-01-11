package com.linda.cluster.redis.client;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;

public interface JedisClient extends BinaryJedisCommands,JedisCommands{

	public void close();
	
}
