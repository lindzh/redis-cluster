package com.linda.cluster.redis.keepalived.conf;

import java.util.List;

import lombok.Data;

@Data
public class RedisCluster {
	private String clusterName;
	private List<HostAndPort> nodes;

}
