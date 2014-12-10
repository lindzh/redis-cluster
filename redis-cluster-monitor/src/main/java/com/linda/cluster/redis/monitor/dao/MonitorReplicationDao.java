package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.MonitorReplication;

public interface MonitorReplicationDao {
	
	public int add(MonitorReplication cpu);
	
	public List<MonitorReplication> getByProductAndTime(long productId,long start,long end,int limit,int offset);
	
	public int getCountByProductAndTime(long productId,long start,long end);
	
	public List<MonitorReplication> getByClusterAndTime(long clusterId,long start,long end,int limit,int offset);
	
	public int getCountByClusterAndTime(long clusterId,long start,long end);
	
	public List<MonitorReplication> getByNodeAndTime(long nodeId,long start,long end,int limit,int offset);
	
	public int getCountByNodeAndTime(long nodeId,long start,long end);
}
