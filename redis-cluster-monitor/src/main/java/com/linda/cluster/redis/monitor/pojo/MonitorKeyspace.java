package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorKeyspace extends MonitorPartBase {
	
	private long id;
	
	private int databaseId;
	
	private int keys;
	private int expires;
	private int avg_ttl;
	
}
