package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorMemoryDao;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;

@Service
public class RedisMonitorMemoryService {
	
	@Autowired
	private MonitorMemoryDao monitorMemoryDao;
	
	public MonitorMemory add(MonitorMemory memory){
		int add = monitorMemoryDao.add(memory);
		return add>0?memory:null;
	}

}
