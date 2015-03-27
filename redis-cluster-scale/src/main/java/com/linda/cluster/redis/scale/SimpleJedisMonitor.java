package com.linda.cluster.redis.scale;

import redis.clients.jedis.JedisMonitor;

public class SimpleJedisMonitor extends JedisMonitor{

	@Override
	public void onCommand(String command) {
		
	}

}
