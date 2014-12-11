package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorStatDao;
import com.linda.cluster.redis.monitor.pojo.MonitorStat;

@Service
public class RedisMonitorStatService {
	@Autowired
	private MonitorStatDao monitorStatDao;
	
	public MonitorStat add(MonitorStat stat){
		int add = monitorStatDao.add(stat);
		return add>0?stat:null;
	}
}
