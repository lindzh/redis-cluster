package com.linda.cluster.redis.keepalived.redis;

public interface RedisAlivedListener {
	
	public void onConnected(RedisAliveBase redis);
	
	public void onClose(RedisAliveBase redis);
	
	public void onException(RedisAliveBase redis,Exception e);
	
	public void onInfo(RedisAliveBase redis,String info);

}
