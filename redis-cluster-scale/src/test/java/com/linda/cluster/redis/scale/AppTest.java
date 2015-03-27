package com.linda.cluster.redis.scale;

import java.io.Serializable;

import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.linda.cluster.redis.client.serializer.SimpleJdkSerializer;

@Data
public class AppTest implements Serializable{
	
	private static final long serialVersionUID = -4903359646373299487L;
	private String name;
	
	public AppTest(String name){
		this.name = name;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SimpleJdkSerializer serializer = new SimpleJdkSerializer();
		String host = "192.168.139.129";
		int port = 7770;
		JedisPool pool = new JedisPool(host,port);
		Jedis jedis = pool.getResource();
		String byteKey = "byte-";
		String keyPrefix = "key-";
		String listPrefix = "list-";
		String setPrefix = "set-";
		int index = 16000;
		int end = 16100;
		while(index<end){
			byte[] value = serializer.serializeValue(new AppTest(byteKey+"value-"+index));
			System.out.println(ScaleUtils.byte2hex(value));
			jedis.set(serializer.serializeKey(byteKey+index),value);
			jedis.set(keyPrefix+index, keyPrefix+"value_"+index);
			jedis.lpush(listPrefix+index, listPrefix+"value"+index);
			jedis.lpush(listPrefix+index, listPrefix+"value"+index+1);
			jedis.lpush(listPrefix+index, listPrefix+"value"+index+2);
			jedis.lpush(listPrefix+index, listPrefix+"value"+index+3);
			jedis.lpush(listPrefix+index, listPrefix+"value"+index+4);
			jedis.sadd(setPrefix+index, setPrefix+"value"+index);
			jedis.sadd(setPrefix+index, setPrefix+"value"+index+1);
			jedis.sadd(setPrefix+index, setPrefix+"value"+index+2);
			index++;
			Thread.currentThread().sleep(500);
		}
		jedis.close();
		pool.destroy();
		System.exit(0);
	}
	
}
