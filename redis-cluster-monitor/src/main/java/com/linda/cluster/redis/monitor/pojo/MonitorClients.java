package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorClients extends MonitorPartBase{
	
	private long id;
	//连接客户端数量
	private int connected_clients;
	
	private int client_longest_output_list;
	
	private int client_biggest_input_buf;
	//阻塞客户端数量
	private int blocked_clients;

}
