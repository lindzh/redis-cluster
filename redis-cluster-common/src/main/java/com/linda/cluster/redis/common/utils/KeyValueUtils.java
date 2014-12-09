package com.linda.cluster.redis.common.utils;

import com.linda.cluster.redis.common.bean.KeyValueBean;

public class KeyValueUtils {
	
	public static KeyValueBean toKeyValue(String line,String split){
		if(line!=null){
			String[] strs = line.trim().split(split);
			if(strs.length==2){
				return new KeyValueBean(strs[0],strs[1]);
			}
		}
		return null;
	}
}
