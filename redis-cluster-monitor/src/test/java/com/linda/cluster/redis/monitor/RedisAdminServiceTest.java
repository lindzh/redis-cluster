package com.linda.cluster.redis.monitor;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.service.RedisClusterAdminService;
import com.linda.cluster.redis.monitor.start.AbstractSpringBootStrap;

public class RedisAdminServiceTest extends AbstractSpringBootStrap implements Service{
	
	@Resource
	private RedisClusterAdminService redisClusterAdminService;
	@Resource
	private RedisMonitor redisMonitor;

	@Before
	public void startup() {
		super.startup();
	}

	public void shutdown() {
		super.shutdown();
	}
	
	@Test
	public void addProductNode() throws InterruptedException{
//		long productId = 0;
//		long clusterId = 0;
//		String host = "127.0.0.1";
//		int port = 6381;
//		
//		Product product = new Product();
//		product.setName("linda-local3-redis");
//		Product addp = redisClusterAdminService.addProduct(product);
//		productId = addp.getId();
//		
//		Cluster cluster = new Cluster();
//		cluster.setClusterName("local3-cache-shard1");
//		cluster.setProductId(productId);
//		Cluster addc = redisClusterAdminService.addCluster(cluster);
//		clusterId = addc.getId();
//		
//		RedisNode node = new RedisNode();
//		node.setProductId(productId);
//		node.setClusterId(clusterId);
//		node.setHost(host);
//		node.setPort(port);
//		node.setName("cache3-shard1-node0");
//		RedisNode addn = redisClusterAdminService.addRedisNode(node);
		
		List<Product> list = redisClusterAdminService.getAllProducts(true);
		System.out.println(JSONUtils.toJson(list));
		redisMonitor.startMonitor();
		Thread.currentThread().sleep(1000000L);
	}
	

}
