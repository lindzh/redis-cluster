package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.MonitorPersitence;

public interface MonitorPersistenceDao {

	public int add(MonitorPersitence cpu);
	
	public List<MonitorPersitence> getByProductAndTime(long productId,long start,long end,int limit,int offset);
	
	public int getCountByProductAndTime(long productId,long start,long end);
	
	public List<MonitorPersitence> getByClusterAndTime(long clusterId,long start,long end,int limit,int offset);
	
	public int getCountByClusterAndTime(long clusterId,long start,long end);
	
	public List<MonitorPersitence> getByNodeAndTime(long nodeId,long start,long end,int limit,int offset);
	
	public int getCountByNodeAndTime(long nodeId,long start,long end);
	
}
