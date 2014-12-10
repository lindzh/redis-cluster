package com.linda.cluster.redis.monitor.pojo;

import java.util.List;

import lombok.Data;

@Data
public class Cluster {
	private long id;
	private long productId;
	private String clusterName;
	
	private List<RedisNode> nodes;

}
