package com.linda.cluster.redis.client;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import junit.framework.TestCase;

public class AppTest extends TestCase {
	
	public static void main(String[] args) {
		JedisSentinelPool pool = new JedisSentinelPool("", null);
		pool.getResource();
		JedisPool jedisPool = new JedisPool("", 123);
		jedisPool.getResource();
		
	}
	
	
}
