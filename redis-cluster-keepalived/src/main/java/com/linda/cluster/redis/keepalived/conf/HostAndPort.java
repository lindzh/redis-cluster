package com.linda.cluster.redis.keepalived.conf;

import lombok.Data;

@Data
public class HostAndPort {
	private String name;
	private String host;
	private int port;

}
