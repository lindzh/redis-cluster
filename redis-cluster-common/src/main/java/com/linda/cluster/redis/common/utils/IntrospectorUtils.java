package com.linda.cluster.redis.common.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * utils for java bean field set from properties config file
 * @author hzlindzh
 *
 */
public class IntrospectorUtils {
	
	private static Logger logger = Logger.getLogger(IntrospectorUtils.class);
	
	public static <T> T getInstance(Class<T> clazz,Properties properties){
		try {
			T object = clazz.newInstance();
			IntrospectorUtils.setProperties(object, properties);
			return object;
		} catch (InstantiationException e) {
			logger.error("InstantiationException "+e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException "+e.getMessage());
		}
		return null;
	}
	
	public static void setProperties(Object obj,Properties prop){
		List<Field> fields = getAllDeclearedFields(obj.getClass());
		for(Field f:fields){
			String v = prop.getProperty(f.getName());
			if(v!=null){
				try {
					v = v.trim();
					f.setAccessible(true);
					f.set(obj, convertValue(f.getType(), v));
				} catch (IllegalArgumentException e) {
					logger.error("IllegalArgumentException field "+f.getName()+" "+obj.getClass()+" "+e.getMessage());
				} catch (IllegalAccessException e) {
					logger.error("IllegalAccessException field "+f.getName()+" "+obj.getClass()+" "+e.getMessage());
				}
			}
		}
	}
	
	public static List<Field> getAllDeclearedFields(Class clazz){
		List<Field> list = new LinkedList<Field>();
		if(clazz!=Object.class){
			list.addAll(Arrays.asList(clazz.getDeclaredFields()));
			list.addAll(getAllDeclearedFields(clazz.getSuperclass()));
		}
		return list;
	}
	
	public static Object convertValue(Class need,String value){
		if(need==byte.class||need==Byte.class){
			return Byte.parseByte(value);
		}else if(need==short.class||need==Short.class){
			return Short.parseShort(value);
		}else if(need==int.class||need==Integer.class){
			return Integer.parseInt(value);
		}else if(need==long.class||need==Long.class){
			return Long.parseLong(value);
		}else if(need==boolean.class||need==Boolean.class){
			return Boolean.parseBoolean(value);
		}else if(need==float.class||need==Float.class){
			return Float.parseFloat(value);
		}else if(need==double.class||need==Double.class){
			return Double.parseDouble(value);
		}else if(need==char.class||need==Character.class){
			return value.trim().charAt(0);
		}else{
			return value;
		}
	}
}
