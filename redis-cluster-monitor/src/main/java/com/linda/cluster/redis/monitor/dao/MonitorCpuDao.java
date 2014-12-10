package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.MonitorCpu;

public interface MonitorCpuDao {

	public int add(MonitorCpu cpu);
	
	public List<MonitorCpu> getByProductAndTime(long productId,long start,long end,int limit,int offset);
	
	public int getCountByProductAndTime(long productId,long start,long end);
	
	public List<MonitorCpu> getByClusterAndTime(long clusterId,long start,long end,int limit,int offset);
	
	public int getCountByClusterAndTime(long clusterId,long start,long end);
	
	public List<MonitorCpu> getByNodeAndTime(long nodeId,long start,long end,int limit,int offset);
	
	public int getCountByNodeAndTime(long nodeId,long start,long end);
}
