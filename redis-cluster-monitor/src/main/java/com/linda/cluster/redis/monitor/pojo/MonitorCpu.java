package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorCpu extends MonitorPartBase {
	
	private long id;
	//redis server sys的cpu使用率
	private float used_cpu_sys;//单位????
	//redis server user 的cpu使用率
	private float used_cpu_user;
	//redis server sys后台进程的cpu使用率
	private float used_cpu_sys_children;
	//redis server user后台进程的cpu使用率
	private float used_cpu_user_children;
}
