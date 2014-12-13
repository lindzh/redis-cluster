package com.linda.cluster.redis.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostAndPort {
	
	private String name;
	private String host;
	private int port;
	private boolean alive;//节点是否存活，用于keepalived info
	private String master;
	
	
	
}
