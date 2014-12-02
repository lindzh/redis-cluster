package com.linda.cluster.redis.keepalived.conf;

import java.util.List;

import lombok.Data;

@Data
public class RedisProduct {
	private String productName;
	private String zkPassword;
	private int pingInterval;//发送ping 命令时间
	private int infoInterval;//发送info命令时间
	private int redisFailOverPaxos = 2;//default failover value 2
	private List<RedisCluster> clusters;

}
