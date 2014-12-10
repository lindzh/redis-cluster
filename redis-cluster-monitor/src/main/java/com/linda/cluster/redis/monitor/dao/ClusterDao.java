package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.Cluster;

public interface ClusterDao {

	public int add(Cluster cluster);
	
	public Cluster getById(long clusterId);
	
	public List<Cluster> getByProductId(long productId);
	
	public int deleteById(long id);
}
