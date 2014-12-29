package com.linda.cluster.redis.client.linda;

import redis.clients.jedis.Jedis;

public class SimpleJedisTemplate extends AbstractJedisTemplate{

	@Override
	protected Jedis getResource(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void returnResource(String key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void returnBrokenResource(String key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Jedis getResource(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void returnResource(byte[] key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void returnBrokenResource(byte[] key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

}
