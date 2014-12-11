package com.linda.cluster.redis.monitor.start;

import javax.annotation.Resource;

import com.linda.cluster.redis.monitor.service.RedisClusterAdminService;
import com.linda.cluster.redis.monitor.service.RedisInfoDataService;

public class AbstractMonitorBootStrap extends AbstractSpringBootStrap{

	@Resource
	protected RedisClusterAdminService redisClusterAdminService;
	@Resource
	protected RedisInfoDataService redisInfoDataService;
	
	public static String getValue(String arg,String prefix){
		if(arg.length()>prefix.length()){
			return arg.substring(prefix.length());
		}
		return null;
	}
	
	public static long parseLong(String str,long defaultV){
		if(str!=null){
			return Long.parseLong(str);
		}
		return defaultV;
	}
	
}
