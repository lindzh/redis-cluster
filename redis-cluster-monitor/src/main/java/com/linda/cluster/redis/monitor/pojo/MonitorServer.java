package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorServer extends MonitorPartBase{
	
	private long id;
	//redis版本
	private String redis_version;
	
	private String redis_git_sha1;
	
	private String redis_git_dirty;
	//构建ID
	private String redis_build_id;
	//运行模式
	private String redis_mode;
	//操作系统
	private String os;
	//安装架构
	private int arch_bits;
	//网络分发模型
	private String multiplexing_api;
	//gcc版本号
	private String gcc_version;
	//进程ID
	private int process_id;
	//运行ID
	private String run_id;
	//端口号
	private int tcp_port;
	//在线时间
	private long uptime_in_seconds;
	//在线时间按天计算
	private long uptime_in_days;
	
	private String hz;
	
	private String lru_clock;
	//配置文件
	private String config_file;
	
}
