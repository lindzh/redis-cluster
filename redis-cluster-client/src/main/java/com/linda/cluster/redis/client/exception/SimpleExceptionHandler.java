package com.linda.cluster.redis.client.exception;

import org.apache.log4j.Logger;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

public class SimpleExceptionHandler implements ClusterExceptionHandler{

	private Logger logger = Logger.getLogger(SimpleExceptionHandler.class);
	
	@Override
	public void handleException(Jedis jedis, Exception e) {
		Client client = jedis.getClient();
		logger.error("node exception:"+client.getHost()+":"+client.getPort()+"  "+e.getMessage());
		e.printStackTrace();
	}
}
