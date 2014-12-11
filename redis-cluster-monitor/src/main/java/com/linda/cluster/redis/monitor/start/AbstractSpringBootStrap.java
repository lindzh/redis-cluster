package com.linda.cluster.redis.monitor.start;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.linda.cluster.redis.common.Service;

public class AbstractSpringBootStrap implements Service{
	
	private ApplicationContext apc;
	
	private List<String> configLocations;
	
	private void loadConfigLocations(){
		configLocations = new ArrayList<String>();
		configLocations.add("classpath:mybatis-config.xml");
		configLocations.add("classpath:spring-context-config.xml");
	}
	
	private void setIoc(){
		List<Field> fields = getResourceFields(this.getClass());
		for(Field field:fields){
			Resource resource = field.getAnnotation(Resource.class);
			if(resource!=null){
				try {
					field.setAccessible(true);
					field.set(this,apc.getBean(field.getType()));
				} catch (BeansException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<Field> getResourceFields(Class clazz){
		ArrayList<Field> list = new ArrayList<Field>();
		if(clazz!=AbstractSpringBootStrap.class&&clazz!=Object.class){
			Field[] fields = clazz.getDeclaredFields();
			for(Field field:fields){
				Resource resource = field.getAnnotation(Resource.class);
				if(resource!=null){
					list.add(field);
				}
			}
		}else{
			return Collections.EMPTY_LIST;
		}
		list.addAll(getResourceFields(clazz.getSuperclass()));
		return list;
	}
	
	public void initSuper(){
		loadConfigLocations();
		apc = new ClassPathXmlApplicationContext(configLocations.toArray(new String[0]));
		setIoc();
	}
	
	
	public <T> T getBean(String name,Class<T> type){
		return apc.getBean(name, type);
	}

	@Override
	public void startup() {
		this.initSuper();
	}

	@Override
	public void shutdown() {
		
	}
}
