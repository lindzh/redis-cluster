package com.linda.cluster.redis.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


public class JSONUtils {

	private static ObjectMapper objMapper = new ObjectMapper();
	private static Logger logger = Logger.getLogger(JSONUtils.class);

	public static String toJson(Object obj) {
		try {
			return objMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("json write exception " + obj.toString());
		}
		return null;
	}

	public static <T> T fromJson(String json, Class<T> clz) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return objMapper.readValue(json, clz);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("read json exception " + json);
		}
		return null;
	}

	public static <T> T fromJson(String json, TypeReference<T> typeReference) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return objMapper.readValue(json, typeReference);
		} catch (Exception e) {
			logger.error("read json exception " + json);
		}
		return null;
	}

	/**
	 * 只能取出不包含数组的部分
	 * @param json
	 * @param propertyName
	 * @param clazz
	 * @return
	 */
	public static <T> T getProperty(String json, String propertyName, Class<T> clazz) {
		try {
			JsonNode readTree = objMapper.readTree(json);
			String[] hirarchyProperties = propertyName.split("\\.");
			int i = 0;
			for (String property : hirarchyProperties) {
				i++;
				if (readTree.has(property)) {
					readTree = readTree.get(property);
				} else {
					if (i != hirarchyProperties.length) {
						logger.error("encounter unexpected internal Property:" + propertyName + "  json: " + json + " clz:" + clazz.getName());
					} else {
						return null;
					}
				}
			}
			if (clazz == Long.class) {
				return (T) new Long(readTree.asLong());
			} else if (clazz == Integer.class) {
				return (T) new Integer(readTree.asInt());
			} else if (clazz == Float.class) {
				return (T) new Float(readTree.asDouble());
			} else if (clazz == Double.class) {
				return (T) new Double(readTree.asDouble());
			} else if (clazz == String.class) {
				return (T) readTree.asText();
			} else if (clazz == Boolean.class) {
				return (T) Boolean.valueOf(readTree.asBoolean());
			} else {
				return (T) readTree;
			}
		} catch (Exception e) {
			logger.error("getProperty properites:" + propertyName + "  json: " + json + " clz:" + clazz.getName(), e);
		}
		return null;
	}
	
}
