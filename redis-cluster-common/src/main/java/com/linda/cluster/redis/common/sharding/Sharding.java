package com.linda.cluster.redis.common.sharding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * product 节点数据部分存储sharding映射表
 * @author linda
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sharding {
	private int from;
	private int to;
	private String node;

}
