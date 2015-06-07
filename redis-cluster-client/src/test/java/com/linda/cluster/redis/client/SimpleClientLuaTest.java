package com.linda.cluster.redis.client;

import java.util.ArrayList;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class SimpleClientLuaTest {
	
	public static void main(String[] args2) {
		
		JedisPool jedisPool = new JedisPool("192.168.139.129",7770);
		Jedis jedis = jedisPool.getResource();
		String lua = "local exist = redis.call('EXISTS',KEYS[1]) "+
					 "if exist==1 then "+
					 "	redis.call('set',KEYS[1],ARGV[1]) "+
					 "	return '1' "+
					 "else return '0' "+
					 "end";
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("lin");
		ArrayList<String> args = new ArrayList<String>();
		args.add("hello world");
		Object result = jedis.eval(lua, keys, args);
		System.out.println("result:"+result);
		
		int index = 100000;
		while(index<200000){
			String key = "key_"+index;
			String value = "val_"+index;
			jedis.set(key, value);
			index++;
		}
		jedis.close();
	}
}
