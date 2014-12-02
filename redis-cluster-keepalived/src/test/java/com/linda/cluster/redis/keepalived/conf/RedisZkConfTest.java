package com.linda.cluster.redis.keepalived.conf;

import com.linda.cluster.redis.common.utils.JSONUtils;

public class RedisZkConfTest {
	
	public static void main(String[] args) {
		String filename = "classpath:keepalived-conf.json";
		RedisZkConf zkConf = RedisZkConf.loadRedisZkConf(filename);
		System.out.println(JSONUtils.toJson(zkConf));
	}

}
