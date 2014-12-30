package com.linda.cluster.redis.client.cluster;

import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Hashing;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.utils.SlotUtils;


public class ClusterJedisTemplate extends JedisTemplate{
	
	private ClusterRedisConnectionHandler connectionHandler;
	
	private KeySlotInfo slotInfo;
	
	private Hashing hashing = Hashing.MD5;
	
	private Logger logger = Logger.getLogger(ClusterJedisTemplate.class);
	
	public ClusterJedisTemplate(JedisPoolConfig poolConfig,List<HostAndPort> zkhosts,String product){
		connectionHandler = new ClusterRedisConnectionHandler(poolConfig,zkhosts,product,null,null);
	}
	
	public ClusterJedisTemplate(JedisPoolConfig poolConfig,List<HostAndPort> zkhosts,String product,String password){
		connectionHandler = new ClusterRedisConnectionHandler(poolConfig,zkhosts,product,password,null);
	}
	
	public ClusterJedisTemplate(JedisPoolConfig poolConfig,List<HostAndPort> zkhosts,String product,String password,String basepath){
		connectionHandler = new ClusterRedisConnectionHandler(poolConfig,zkhosts,product,password,basepath);
	}
	
	public KeySlotInfo getSlotInfo() {
		return slotInfo;
	}

	public void setSlotInfo(KeySlotInfo slotInfo) {
		this.slotInfo = slotInfo;
	}

	@Override
	protected Jedis getResource(String key) {
		long hash = hashing.hash(key);
		int slot = SlotUtils.slot(hash);
		Jedis jedis = connectionHandler.getConnectionFromSlot(slot);
		if(slotInfo!=null){
			slotInfo.info(key, slot, jedis);
		}
		return jedis;
	}

	@Override
	protected void returnResource(Jedis jedis) {
		connectionHandler.returnConnection(jedis);
	}

	@Override
	protected void returnBrokenResource(Jedis jedis) {
		connectionHandler.returnBrokenConnection(jedis);
	}

	@Override
	protected Jedis getResource(byte[] key) {
		long hash = hashing.hash(key);
		int slot = SlotUtils.slot(hash);
		logger.debug("key:"+key+" slot:"+slot);
		return connectionHandler.getConnectionFromSlot(slot);
	}

	@Override
	public void close() {
		connectionHandler.close();
	}
}
