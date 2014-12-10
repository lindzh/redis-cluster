package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;

public interface MonitorKeyspaceDao {
	
	//先合并再加入DB
	public int add(MonitorKeyspace keyspace);
	
	public List<MonitorKeyspace> getByProductAndTime(@Param("productId")long productId,@Param("start")long start,@Param("end")long end,@Param("limit")int limit,@Param("offset")int offset);
	
	public int getCountByProductAndTime(@Param("productId")long productId,@Param("start")long start,@Param("end")long end);
	
	public List<MonitorKeyspace> getByClusterAndTime(@Param("clusterId")long clusterId,@Param("start")long start,@Param("end")long end,@Param("limit")int limit,@Param("offset")int offset);
	
	public int getCountByClusterAndTime(@Param("clusterId")long clusterId,@Param("start")long start,@Param("end")long end);
	
	public List<MonitorKeyspace> getByNodeAndTime(@Param("nodeId")long nodeId,@Param("start")long start,@Param("end")long end,@Param("limit")int limit,@Param("offset")int offset);
	
	public int getCountByNodeAndTime(@Param("nodeId")long nodeId,@Param("start")long start,@Param("end")long end);
}
