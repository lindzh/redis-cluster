package com.linda.cluster.redis.client.cluster;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SimpleJedisTemplate extends JedisTemplate{
	
	private JedisPool jedisPool;
	private JedisPoolConfig jedisPoolConfig;
	private String host;
	private int port = 6379;//default redis server port
	
	public SimpleJedisTemplate(String host,int port){
		this.host = host;
		this.port = port;
		this.init();
	}
	
	public SimpleJedisTemplate(String host){
		this.host = host;
		this.init();
	}
	
	public SimpleJedisTemplate(JedisPoolConfig jedisPoolConfig,String host,int port){
		this.host = host;
		this.port = port;
		this.jedisPoolConfig = jedisPoolConfig;
		this.init();
	}
	
	private void init(){
		if(jedisPoolConfig==null){
			jedisPool = new JedisPool(host,port);
		}else{
			jedisPool = new JedisPool(jedisPoolConfig,host,port);
		}
	}

	@Override
	protected Jedis getResource(String key) {
		return jedisPool.getResource();
	}

	@Override
	protected void returnResource(Jedis jedis) {
		jedisPool.returnResource(jedis);
	}

	@Override
	protected void returnBrokenResource(Jedis jedis) {
		jedisPool.returnBrokenResource(jedis);
	}

	@Override
	protected Jedis getResource(byte[] key) {
		return jedisPool.getResource();
	}

	@Override
	public void close() {
		jedisPool.destroy();
	}

}
