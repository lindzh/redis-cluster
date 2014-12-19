package com.linda.cluster.redis.keepalived.redis;

import com.linda.cluster.redis.common.bean.HostAndPort;

public class RedisKeepAlivedTest {
	
	public static void main(String[] args) {
		final RedisKeepAlived alived = new RedisKeepAlived();
		alived.setPingInterval(5000);
		alived.setRedisHost(new HostAndPort("test","127.0.0.1",6379, false, null,null,null));
		alived.addRedisListener(new RedisAlivedListener(){
			@Override
			public void onConnected(RedisAliveBase redis) {
				System.out.println("connected:"+redis.getRedisHost().toString());
			}

			@Override
			public void onClose(RedisAliveBase redis) {
				System.out.println("closed:"+redis.getRedisHost().toString());
			}

			@Override
			public void onException(RedisAliveBase redis,Exception e) {
				e.printStackTrace();
				System.out.println("exception:"+redis.getRedisHost().toString());
			}

			@Override
			public void onInfo(RedisAliveBase redis, String info) {
				System.out.println("info:"+redis.getRedisHost().toString()+" info:"+info);
			}
		});
		alived.startup();
	}
}
