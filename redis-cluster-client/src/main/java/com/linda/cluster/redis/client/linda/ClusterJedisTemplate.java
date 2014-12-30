package com.linda.cluster.redis.client.linda;

import java.util.List;

import com.linda.cluster.redis.common.utils.SlotUtils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.util.Hashing;


public class ClusterJedisTemplate extends JedisTemplate{
	
	private ClusterRedisConnectionHandler connectionHandler;
	
	private Hashing hashing = Hashing.MD5;
	
	public ClusterJedisTemplate(List<HostAndPort> zkhosts,String product){
		connectionHandler = new ClusterRedisConnectionHandler(zkhosts,product,null,null);
	}
	
	public ClusterJedisTemplate(List<HostAndPort> zkhosts,String product,String password){
		connectionHandler = new ClusterRedisConnectionHandler(zkhosts,product,password,null);
	}
	
	public ClusterJedisTemplate(List<HostAndPort> zkhosts,String product,String password,String basepath){
		connectionHandler = new ClusterRedisConnectionHandler(zkhosts,product,password,basepath);
	}

	@Override
	protected Jedis getResource(String key) {
		long hash = hashing.hash(key);
		int slot = SlotUtils.slot(hash);
		return connectionHandler.getConnectionFromSlot(slot);
	}

	@Override
	protected void returnResource(String key, Jedis jedis) {
		connectionHandler.returnConnection(jedis);
	}

	@Override
	protected void returnBrokenResource(String key, Jedis jedis) {
		connectionHandler.returnBrokenConnection(jedis);
	}

	@Override
	protected Jedis getResource(byte[] key) {
		long hash = hashing.hash(key);
		int slot = SlotUtils.slot(hash);
		return connectionHandler.getConnectionFromSlot(slot);
	}

	@Override
	protected void returnResource(byte[] key, Jedis jedis) {
		connectionHandler.returnConnection(jedis);
	}

	@Override
	protected void returnBrokenResource(byte[] key, Jedis jedis) {
		connectionHandler.returnBrokenConnection(jedis);
	}

	@Override
	public void close() {
		connectionHandler.close();
	}
}
