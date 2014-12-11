package com.linda.cluster.redis.common.utils;

import java.util.ArrayList;
import java.util.List;

import com.linda.cluster.redis.common.bean.KeyValueBean;

public class KeyValueUtils {
	
	public static KeyValueBean toKeyValue(String line,String split){
		if(line!=null){
			int idx = line.indexOf(split);
			if(idx>0&&idx<line.length()){
				String key = line.substring(0,idx);
				String value = line.substring(idx+1);
				return new KeyValueBean(key, value);
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
					KeyValueBean bean = KeyValueUtils.toKeyValue(kv, kvSplit);
					if(bean!=null){
						list.add(bean);
					}
				}
			}
		}
		return list;
	}
}
