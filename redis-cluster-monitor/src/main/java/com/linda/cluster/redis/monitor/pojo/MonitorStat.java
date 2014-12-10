package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorStat {

	private long id;
	//产品ID
	private long productId;
	//集群ID
	private long clusterId;
	//节点ID
	private long redisNodeId;
	
	private int total_connections_received;
	
	private long total_commands_processed;
	
	private int instantaneous_ops_per_sec;
	
	private int rejected_connections;
	
	private int sync_full;
	
	private int sync_partial_ok;
	
	private int sync_partial_err;
	
	private long expired_keys;
	
	private long evicted_keys;
	
	private long keyspace_hits;
	
	private long keyspace_misses;
	
	private int pubsub_channels;
	
	private int pubsub_patterns;
	
	private int latest_fork_usec;
}
