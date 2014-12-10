package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorKeyspace {
	
	private long id;
	//产品ID
	private long productId;
	//集群ID
	private long clusterId;
	//节点ID
	private long redisNodeId;
	
	private int databaseId;
	
	private int keys;
	private int expires;
	private int avg_ttl;
	
}
