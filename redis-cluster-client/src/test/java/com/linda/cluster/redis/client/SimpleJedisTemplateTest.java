package com.linda.cluster.redis.client;

import com.linda.cluster.redis.client.cluster.JedisTemplate;
import com.linda.cluster.redis.client.cluster.SimpleJedisTemplate;

public class SimpleJedisTemplateTest {
	
	public static void main(String[] args) {
		JedisTemplate jedisTemplate = new SimpleJedisTemplate("127.0.0.1");
		jedisTemplate.set("testcmd", "534534534");
		String string = jedisTemplate.get("testcmd");
		System.out.println(string);
		jedisTemplate.rpush("push", "1");
		jedisTemplate.rpush("push", "2");
		jedisTemplate.rpush("push", "3");
		jedisTemplate.rpush("push", "4");
		jedisTemplate.rpush("push", "5");
		int i = 0;
		while(i<5){
			String lpop = jedisTemplate.lpop("push");
			System.out.println(lpop);
			i++;
		}
		jedisTemplate.close();
	}
}
