package com.linda.cluster.redis.client.exception;

import redis.clients.jedis.Jedis;

public interface ClusterExceptionHandler {
	
	public void handleException(Jedis jedis,Exception e);

}
