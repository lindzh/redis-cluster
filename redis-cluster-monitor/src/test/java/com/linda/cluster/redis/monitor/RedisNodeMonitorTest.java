package com.linda.cluster.redis.monitor;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;
import com.linda.cluster.redis.monitor.service.RedisInfoDataService;

public class RedisNodeMonitorTest {
	
	public static void main(String[] args) {
		
		long productId = 1234;
		long clusterId = 42342;
		long nodeId = 432432;
		
		String host = "yixin12.server.163.org";
		int port = 22121;
		
		Product product = new Product();
		product.setId(productId);
		product.setName("yixin");
		
		Cluster cluster = new Cluster();
		cluster.setClusterName("ha-queue1");
		cluster.setProductId(productId);
		cluster.setId(clusterId);
		
		RedisNode node = new RedisNode();
		node.setClusterId(clusterId);
		node.setHost(host);
		node.setId(nodeId);
		node.setPort(port);
		node.setProductId(productId);
		node.setName("ha-queue1-node1");
		
		HostAndPort redisHostAndPort = new HostAndPort(host, port);
		Jedis jedis = new Jedis(redisHostAndPort.getHost(),redisHostAndPort.getPort());
		//jedis.connect();
		
		RedisMonitor monitor = new RedisMonitor();
		monitor.setInfoDataService(new RedisInfoDataService());
		
		monitor.infoNode(jedis, product, cluster, node);
	
		jedis.close();
	}

}
