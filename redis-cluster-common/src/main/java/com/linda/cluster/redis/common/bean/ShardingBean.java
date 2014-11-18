package com.linda.cluster.redis.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShardingBean {

	private String bucketName;
	private String nodeName;
}
