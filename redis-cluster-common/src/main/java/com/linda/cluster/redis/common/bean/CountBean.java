package com.linda.cluster.redis.common.bean;

import lombok.Data;

@Data
public class CountBean {
	
	private int count;
	
	public void incr(){
		count++;
	}

}
