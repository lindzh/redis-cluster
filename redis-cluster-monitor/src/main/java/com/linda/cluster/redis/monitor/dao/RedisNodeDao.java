package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.RedisNode;

public interface RedisNodeDao {
	
	public int add(RedisNode node);
	
	public RedisNode getById(long id);
	
	public List<RedisNode> getByClusterId(long productId,long clusterId);
	
	public List<RedisNode> getByProductId(long productId);
	
	public int deleteById(long id);

}
