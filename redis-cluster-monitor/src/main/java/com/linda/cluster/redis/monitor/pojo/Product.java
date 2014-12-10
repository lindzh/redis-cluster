package com.linda.cluster.redis.monitor.pojo;

import java.util.List;

import lombok.Data;

@Data
public class Product {
	private long id;
	private String name;
	
	private List<Cluster> clusters;
	
}
