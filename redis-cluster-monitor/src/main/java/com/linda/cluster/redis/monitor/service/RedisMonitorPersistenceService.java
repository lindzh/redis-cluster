package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorPersistenceDao;
import com.linda.cluster.redis.monitor.pojo.MonitorPersistence;

@Service
public class RedisMonitorPersistenceService {
	@Autowired
	private MonitorPersistenceDao monitorPersistenceDao;
	
	public MonitorPersistence add(MonitorPersistence persistence){
		int add = monitorPersistenceDao.add(persistence);
		return add>0?persistence:null;
	}

}
