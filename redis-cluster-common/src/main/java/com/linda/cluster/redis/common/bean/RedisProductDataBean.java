package com.linda.cluster.redis.common.bean;

import java.util.List;

import lombok.Data;

import com.linda.cluster.redis.common.sharding.Sharding;

@Data
public class RedisProductDataBean {
	//产品id，监控中映射
	private long id;
	//sharding映射表
	private List<Sharding> sharding;
	
	private String backup;
}
