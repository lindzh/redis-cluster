package com.linda.cluster.redis.common.bean;

import lombok.Data;

@Data
public class RedisZookeeperServiceConfig {
	private String servers;
	private String path;
	private String chroot;
	private String username;
	private String password;
	private int zooTimeout;
	private int redisTimeout;
	private int retrytime;
	
	@Data
	public static class RedisClientBean{
		private String host;
		private int port;
		private String password;
	}
	
	public enum RedisEnum{
		Master("master"),
		Slave("slave");
		
		private String name;
		
		RedisEnum(String name){
			this.name = name;
		}
		
		public String getName(String name){
			return name;
		}
		
		public static RedisEnum getByName(String name){
			RedisEnum[] vs = RedisEnum.values();
			for(RedisEnum v:vs){
				if(v.name.equalsIgnoreCase(name)){
					return v;
				}
			}
			return Master;
		}
	}
}
