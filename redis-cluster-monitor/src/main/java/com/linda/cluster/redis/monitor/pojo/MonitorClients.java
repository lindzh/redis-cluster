package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorClients {
	
	private long id;
	//产品ID
	private long productId;
	//集群ID
	private long clusterId;
	//节点ID
	private long redisNodeId;
	//连接客户端数量
	private int connected_clients;
	
	private int client_longest_output_list;
	
	private int client_biggest_input_buf;
	//阻塞客户端数量
	private int blocked_clients;

}
