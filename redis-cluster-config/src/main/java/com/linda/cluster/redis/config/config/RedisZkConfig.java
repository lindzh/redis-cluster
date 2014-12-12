package com.linda.cluster.redis.config.config;

import java.util.List;

import lombok.Data;

import com.linda.cluster.redis.common.bean.HostAndPort;

@Data
public class RedisZkConfig {
	private List<HostAndPort> zkhosts;
	private String redisBasePath;
	
	public String toZkServer(){
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(HostAndPort zkh:zkhosts){
			sb.append(zkh.getHost());
			sb.append(":");
			sb.append(zkh.getPort());
			if(index<zkhosts.size()-1){
				sb.append(",");
			}
			index++;
		}
		return sb.toString();
	}
}
