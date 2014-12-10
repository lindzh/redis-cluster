package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;

public interface MonitorKeyspaceDao {
	
	//先合并再加入DB
	public int add(MonitorKeyspace keyspace);
	
	public List<MonitorKeyspace> getByProductAndTime(long productId,long start,long end,int limit,int offset);
	
	public int getCountByProductAndTime(long productId,long start,long end);
	
	public List<MonitorKeyspace> getByClusterAndTime(long clusterId,long start,long end,int limit,int offset);
	
	public int getCountByClusterAndTime(long clusterId,long start,long end);
	
	public List<MonitorKeyspace> getByNodeAndTime(long nodeId,long start,long end,int limit,int offset);
	
	public int getCountByNodeAndTime(long nodeId,long start,long end);
}
