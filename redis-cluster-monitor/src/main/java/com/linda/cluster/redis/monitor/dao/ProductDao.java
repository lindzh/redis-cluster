package com.linda.cluster.redis.monitor.dao;

import java.util.List;

import com.linda.cluster.redis.monitor.pojo.Product;

public interface ProductDao {
	
	public int add(Product pdt);
	
	public List<Product> getAll();
	
	public Product getById(long id);

	public int deleteById(long id);
}
