package com.linda.cluster.redis.keepalived.redis;

public class RedisDataNode {
	
	public enum RedisState{
		INIT,
		ERROR,
		CONNECTING,
		CONNECTED,
		CLOSE
	}
	
}
