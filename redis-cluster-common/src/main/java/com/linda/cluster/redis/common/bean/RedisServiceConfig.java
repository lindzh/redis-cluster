package com.linda.cluster.redis.common.bean;

import lombok.Data;

@Data
public class RedisServiceConfig {
	
	private String defaultMasterHost;
	private int defaultMasterPort;
	private String defaultSlaveHost;
	private int defaultSlavePort;
	
	private String youdaoMasterHost;
	private int youdaoMasterPort;
	private String youdaoSlaveHost;
	private int youdaoSlavePort;
	
	public enum RedisDomain{
		
		Default("default"),
		Youdao("youdao");
		
		private String domain;
		
		RedisDomain(String domain){
			this.domain = domain;
		}
		
		public String getDomain(){
			return this.domain;
		}
		
		public static RedisDomain getByDomain(String domain){
			RedisDomain[] values = RedisDomain.values();
			for(RedisDomain v:values){
				if(v.domain.equals(domain)){
					return v;
				}
			}
			return RedisDomain.Default;
		}
	}
}
