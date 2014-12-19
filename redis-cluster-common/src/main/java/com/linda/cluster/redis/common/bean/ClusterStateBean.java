package com.linda.cluster.redis.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.annotate.JsonIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterStateBean {
	//集群是否存活
	private boolean alive;

	private String master;
	
	@JsonIgnore
	private Stat stat;
}
