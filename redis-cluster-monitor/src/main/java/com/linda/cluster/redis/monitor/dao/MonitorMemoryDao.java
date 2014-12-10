package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.linda.cluster.redis.monitor.pojo.MonitorMemory;

public interface MonitorMemoryDao {

	public int add(MonitorMemory memory);
	
	public List<MonitorMemory> getByProductAndTime(@Param("productId")long productId,@Param("start")long start,@Param("end")long end,@Param("limit")int limit,@Param("offset")int offset);
	
	public int getCountByProductAndTime(@Param("productId")long productId,@Param("start")long start,@Param("end")long end);
	
	public List<MonitorMemory> getByClusterAndTime(@Param("clusterId")long clusterId,@Param("start")long start,@Param("end")long end,@Param("limit")int limit,@Param("offset")int offset);
	
	public int getCountByClusterAndTime(@Param("clusterId")long clusterId,@Param("start")long start,@Param("end")long end);
	
	public List<MonitorMemory> getByNodeAndTime(@Param("nodeId")long nodeId,@Param("start")long start,@Param("end")long end,@Param("limit")int limit,@Param("offset")int offset);
	
	public int getCountByNodeAndTime(@Param("nodeId")long nodeId,@Param("start")long start,@Param("end")long end);
	
}
