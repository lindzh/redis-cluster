package com.linda.cluster.redis.client.push;

public class JedisClusterException extends RuntimeException{

	private static final long serialVersionUID = 232516377380788562L;

	public JedisClusterException(String message){
		super(message);
	}
}
