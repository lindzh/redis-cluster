package com.linda.cluster.redis.client.transaction;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class JedisOperation {

	private String key;
	private Method method;
	private Object[] args;
	

}
