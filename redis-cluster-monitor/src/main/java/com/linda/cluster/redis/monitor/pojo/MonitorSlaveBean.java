package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorSlaveBean {
	
	private String ip;
	
	private int port;
	
	private String state;
	
	private long offset;
	
	private int lag;

}
