package com.linda.cluster.redis.common.utils;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<KeyValueBean> toKeyValue(String line,String kvSplit,String seperator){
		List<KeyValueBean> list = new ArrayList<KeyValueBean>();
		if(line!=null){
			String[] splits = line.trim().split(seperator);
			if(splits!=null){
				for(String kv:splits){
					String[] kvStrs = kv.split(kvSplit);
					if(kvStrs!=null&&kvStrs.length==2){
						list.add(new KeyValueBean(kvStrs[0], kvStrs[1]));
					}
				}
			}
		}
		return list;
	}
}
