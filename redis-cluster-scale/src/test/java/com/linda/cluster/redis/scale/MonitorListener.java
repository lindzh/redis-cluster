package com.linda.cluster.redis.scale;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisMonitor;
import redis.clients.jedis.JedisPool;

public class MonitorListener{
	
	public static void main(String[] args) {
		String host = "192.168.139.129";
		int port = 7770;
		System.out.println("server:"+host+":"+port);
		JedisPool pool = new JedisPool(host,port);
		Jedis jedis = pool.getResource();
		jedis.monitor(new JedisMonitor(){
			@Override
			public void onCommand(String command) {
				System.out.println(command);
			}
		});
		jedis.close();
		pool.destroy();
	}

}
