package com.linda.cluster.redis.common.bean;

import lombok.Data;

@Data
public class ClusterStateBean {
	//集群是否存活
	private boolean alive;

	private String master;
}
