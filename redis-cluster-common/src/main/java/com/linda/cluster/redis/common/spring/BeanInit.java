package com.linda.cluster.redis.common.spring;

/**
 * bean life cycle
 * @author hzlindzh
 *
 */
public interface BeanInit {
	
	public void init();
	
	public void destroy();
	
}
