package com.linda.cluster.redis.client;

import org.apache.log4j.Logger;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.client.cluster.KeySlotInfo;

public class SimpleSlotInfo implements KeySlotInfo{

	private Logger logger = Logger.getLogger(SimpleSlotInfo.class);
	
	@Override
	public void info(String key, int slot, Jedis jedis) {
		String redisNode = "";
		if(jedis!=null){
			Client client = jedis.getClient();
			redisNode = client.getHost()+":"+client.getPort();
		}
		logger.info("shardingSlot key:"+key+" slot:"+slot+" node:"+redisNode);
	}

}
