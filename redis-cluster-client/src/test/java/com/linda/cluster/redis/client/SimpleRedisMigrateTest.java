package com.linda.cluster.redis.client;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanResult;

public class SimpleRedisMigrateTest {
	
	public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool("192.168.139.129",7770);
		Jedis jedis = jedisPool.getResource();
		String destHost = "192.168.139.129";
		int destPort = 7771;
		ScanResult<String> scanResult = jedis.scan("0");
		String cursor = scanResult.getStringCursor();
		List<String> keys = scanResult.getResult();
		while(cursor!=null&&keys!=null&&keys.size()>0){
			System.out.println("----migrate cursor:"+cursor);
			for(String key:keys){
				jedis.migrate(destHost, destPort, key, 0, 3000);
				System.out.println("migrate:"+key);
			}
			scanResult = jedis.scan(cursor);
			cursor = scanResult.getStringCursor();
			keys = scanResult.getResult();
		}
		jedis.close();
	}
}
