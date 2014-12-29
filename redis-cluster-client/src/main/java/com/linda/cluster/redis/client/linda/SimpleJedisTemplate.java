package com.linda.cluster.redis.client.linda;

import redis.clients.jedis.Jedis;

public class SimpleJedisTemplate extends AbstractJedisTemplate{

	@Override
	public Jedis getResource(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnResource(String key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnBrokenResource(String key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Jedis getResource(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnResource(byte[] key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnBrokenResource(byte[] key, Jedis jedis) {
		// TODO Auto-generated method stub
		
	}

}
