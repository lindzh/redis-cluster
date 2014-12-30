package com.linda.cluster.redis.client.cluster;

import redis.clients.jedis.Jedis;

public interface KeySlotInfo {
	
	public void info(String key,int slot,Jedis jedis);

}
