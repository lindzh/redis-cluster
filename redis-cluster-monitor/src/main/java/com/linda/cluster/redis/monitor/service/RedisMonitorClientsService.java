package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorClientsDao;
import com.linda.cluster.redis.monitor.pojo.MonitorClients;

@Service
public class RedisMonitorClientsService {
	
	@Autowired
	private MonitorClientsDao monitorClientsDao;
	
	public MonitorClients add(MonitorClients clients){
		int add = monitorClientsDao.add(clients);
		if(add>0){
			return clients;
		}
		return null;
	}

}
