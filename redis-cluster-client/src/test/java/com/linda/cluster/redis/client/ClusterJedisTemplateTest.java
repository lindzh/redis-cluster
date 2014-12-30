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
		template.setSlotInfo(new SimpleSlotInfo());
		String keyPrefix = "test-";
		int index = 100;
		while(index<200){
			String key = keyPrefix+index;
			String value = "this is "+key;
			template.set(key, value);
			index++;
		}
		System.out.println("----------------------------");
		template.close();
	}

}
