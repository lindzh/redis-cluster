package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.linda.cluster.redis.monitor.pojo.RedisNode;

public interface RedisNodeDao {
	
	public int add(RedisNode node);
	
	public RedisNode getById(long id);
	
	public List<RedisNode> getByClusterId(@Param("productId")long productId,@Param("clusterId")long clusterId);
	
	public List<RedisNode> getByProductId(long productId);
	
	public int deleteById(long id);

}
