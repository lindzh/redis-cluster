package com.linda.cluster.redis.common.utils;

public class SlotUtils {
	
	public static int slot(long hash){
		long result = hash%256;
		return (int)result;
	}
}
