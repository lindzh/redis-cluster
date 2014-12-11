package com.linda.cluster.redis.config.config;

import java.util.List;

import lombok.Data;
import redis.clients.jedis.HostAndPort;

@Data
public class RedisZkConfig {
	private List<HostAndPort> zkhosts;
	private String redisBasePath;
}
