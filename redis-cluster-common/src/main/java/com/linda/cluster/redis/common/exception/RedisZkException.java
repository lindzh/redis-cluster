package com.linda.cluster.redis.common.exception;

public class RedisZkException extends RuntimeException{
	
	private static final long serialVersionUID = 5572744379554261506L;

	public RedisZkException(String message){
		super(message);
	}
	
	public RedisZkException(Throwable throwable){
		super(throwable);
	}

}
