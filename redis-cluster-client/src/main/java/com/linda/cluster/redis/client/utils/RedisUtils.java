package com.linda.cluster.redis.client.utils;

import java.util.List;

import redis.clients.jedis.HostAndPort;

import com.linda.cluster.redis.common.sharding.Sharding;

public class RedisUtils {

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
	
	public static boolean shardingEqual(Sharding shOld,Sharding shNew){
		if(shOld==null){
			return false;
		}
		if(shOld.getFrom()==shNew.getFrom()&&shOld.getTo()==shNew.getTo()){
			return true;
		}else{
			return false;
		}
	}
	
}
