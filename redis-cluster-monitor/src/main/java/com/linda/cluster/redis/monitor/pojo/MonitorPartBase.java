package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public abstract class MonitorPartBase {
	//产品ID
	private long productId;
	//集群ID
	private long clusterId;
	//节点ID
	private long redisNodeId;
	
	private long addtime;
	
	public MonitorPartBase(){
		this.addtime = System.currentTimeMillis();
	}
	
}
