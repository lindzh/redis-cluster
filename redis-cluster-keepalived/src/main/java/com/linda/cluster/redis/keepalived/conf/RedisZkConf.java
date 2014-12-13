package com.linda.cluster.redis.keepalived.conf;

import java.io.InputStream;
import java.util.List;

import lombok.Data;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.JSONUtils;

@Data
public class RedisZkConf {
	
	private String zkBasePath;
	private List<HostAndPort> zkhosts;
	private List<RedisProduct> products;
	
	public static RedisZkConf loadRedisZkConf(String name){
		InputStream ins = IOUtils.getClassPathInputStream(name);
		String json = IOUtils.toString(ins);
		return JSONUtils.fromJson(json, RedisZkConf.class);
	}
}
