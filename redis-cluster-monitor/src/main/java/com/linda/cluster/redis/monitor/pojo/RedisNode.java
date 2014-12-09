package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class RedisNode {
	private long id;
	private long productId;
	private long clusterId;
	private String name;
	private String host;
	private int port;

}
