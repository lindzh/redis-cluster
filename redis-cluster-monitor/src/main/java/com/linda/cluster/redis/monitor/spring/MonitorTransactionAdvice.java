package com.linda.cluster.redis.monitor.spring;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MonitorTransactionAdvice implements ApplicationContextAware{
	
	private Logger logger = Logger.getLogger(MonitorTransactionAdvice.class);
	
	@Around("execution(@com.linda.cluster.redis.monitor.spring.MonitorTransaction * *(..))")
	public Object processThread(ProceedingJoinPoint pjp) throws Throwable{
		Object result = null;
		Object[] args = pjp.getArgs();
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		return result;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext apc)
			throws BeansException {
		
	}
	
}
