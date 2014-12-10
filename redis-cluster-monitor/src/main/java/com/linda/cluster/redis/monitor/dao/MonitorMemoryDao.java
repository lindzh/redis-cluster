package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.MonitorMemory;

public interface MonitorMemoryDao {

	public int add(MonitorMemory cpu);
	
	public List<MonitorMemory> getByProductAndTime(long productId,long start,long end,int limit,int offset);
	
	public int getCountByProductAndTime(long productId,long start,long end);
	
	public List<MonitorMemory> getByClusterAndTime(long clusterId,long start,long end,int limit,int offset);
	
	public int getCountByClusterAndTime(long clusterId,long start,long end);
	
	public List<MonitorMemory> getByNodeAndTime(long nodeId,long start,long end,int limit,int offset);
	
	public int getCountByNodeAndTime(long nodeId,long start,long end);
	
}
