package com.linda.cluster.redis.config.utils;

import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.config.config.RedisZkConfig;

public class RedisZkUtils {

	public static RedisZkConfig toRedisZkConfig(String configString){
		return JSONUtils.fromJson(configString, RedisZkConfig.class);
	}
	
}
