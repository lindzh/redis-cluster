package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorCpuDao;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;

@Service
public class RedisMonitorCpuService {
	
	@Autowired
	private MonitorCpuDao monitorCpuDao;
	
	public MonitorCpu add(MonitorCpu cpu){
		int add = monitorCpuDao.add(cpu);
		return add>0?cpu:null;
	}
	

}
