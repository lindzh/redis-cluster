package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.MonitorServer;

public interface MonitorServerDao {

	public int add(MonitorServer cpu);
	
	public List<MonitorServer> getByProduct(long productId);
	
	public List<MonitorServer> getByCluster(long clusterId);
	
	public MonitorServer getByNode(long nodeId);
	
}
