package com.linda.cluster.redis.config.sharding;

import lombok.Data;

/**
 * product 节点数据部分存储sharding映射表
 * @author linda
 */
@Data
public class Sharding {
	private int from;
	private int to;
	private String node;

}
