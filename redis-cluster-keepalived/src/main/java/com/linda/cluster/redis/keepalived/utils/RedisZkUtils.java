package com.linda.cluster.redis.keepalived.utils;

import java.util.List;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.utils.JSONUtils;

public class RedisZkUtils {
	
	public static String toString(List<HostAndPort> hosts){
		int size = hosts.size();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(HostAndPort host:hosts){
			sb.append(host.getHost());
			sb.append(":");
			sb.append(host.getPort());
			if(index<size-1){
				sb.append(",");
			}
			index++;
		}
		return sb.toString();
	}

	public static String toString(HostAndPort hostAndPort){
		return JSONUtils.toJson(hostAndPort);
	}
}
