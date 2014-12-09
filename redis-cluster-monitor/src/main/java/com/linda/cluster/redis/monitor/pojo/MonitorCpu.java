package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorCpu {
	
	private long id;
	//产品ID
	private long productId;
	//集群ID
	private long clusterId;
	//节点ID
	private long redisNodeId;
	//redis server sys的cpu使用率
	private float used_cpu_sys;//单位????
	//redis server user 的cpu使用率
	private float used_cpu_user;
	//redis server sys后台进程的cpu使用率
	private float used_cpu_sys_children;
	//redis server user后台进程的cpu使用率
	private float used_cpu_user_children;
}
