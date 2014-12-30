package com.linda.cluster.redis.client;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisPoolConfig;

import com.linda.cluster.redis.client.cluster.ClusterJedisTemplate;
import com.linda.cluster.redis.common.bean.HostAndPort;

public class ClusterJedisTemplateTest {
	
	public static void main(String[] args) {
		String basePath = "/redis-cluster";
		String product = "yixin12";
		String password = null;
		List<HostAndPort> zkhost = new ArrayList<HostAndPort>();
		zkhost.add(new HostAndPort("10.120.151.105",10001));
		zkhost.add(new HostAndPort("10.120.151.105",10002));
		zkhost.add(new HostAndPort("10.120.151.105",10003));
		JedisPoolConfig config = new JedisPoolConfig();
		ClusterJedisTemplate template = new ClusterJedisTemplate(config, zkhost, product, password, basePath);
		template.set("test1", "this is a test");
		template.lpush("testlist", "534");
		template.lpush("testlist", "5343");
		template.lpush("testlist", "22");
		System.out.println("--------------------");
		template.close();
	}

}
