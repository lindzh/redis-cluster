package com.linda.cluster.redis.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyValueBean {
	private String key;
	private String value;

}
