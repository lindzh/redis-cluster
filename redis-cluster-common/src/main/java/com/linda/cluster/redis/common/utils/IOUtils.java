package com.linda.cluster.redis.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class IOUtils {
	
	private static Logger logger = Logger.getLogger(IOUtils.class);
	public static final String CLASS_PATH_PREFIX = "classpath:";
	public static final String FILE_PATH_PREFIX = "file:";

	public static InputStream getClassPathInputStream(String name){
		if(name.startsWith(CLASS_PATH_PREFIX)){
			name = name.substring(CLASS_PATH_PREFIX.length());
		}
		return IOUtils.class.getClassLoader().getResourceAsStream(name);
	}
	
	public static InputStream getFileInputStream(String filename){
		if(filename.startsWith(FILE_PATH_PREFIX)){
			filename = filename.substring(FILE_PATH_PREFIX.length());
		}
		File file = new File(filename);
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException "+e.getMessage());
		}
		return null;
	}
	
	public static Properties loadProperties(InputStream ins){
		Properties properties = new Properties();
		try {
			properties.load(ins);
		} catch (IOException e) {
			logger.error("load properties io error");
		}
		return properties;
	}
	
	public static void closeInputStream(InputStream ins){
		try {
			ins.close();
		} catch (IOException e) {
			logger.error("close inputstream io error");
		}
	}
	
	public static String toString(InputStream ins){
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
		try{
			String line = reader.readLine();
			while(line!=null){
				builder.append(line);
				line = reader.readLine();
			}
		}catch(Exception e){
			logger.error("load inputStream read error");
		}
		return builder.toString();
	}
	
}
