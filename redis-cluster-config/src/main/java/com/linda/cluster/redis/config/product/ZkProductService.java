package com.linda.cluster.redis.config.product;

import java.util.List;

import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.sharding.Sharding;


public class ZkProductService {
	
	private ZooKeeper zookeeper;
	private List<Sharding> shardings;
	private String productname;
	
}
