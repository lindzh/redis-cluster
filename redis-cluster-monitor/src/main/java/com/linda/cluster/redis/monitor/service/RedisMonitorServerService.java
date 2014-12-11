package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorServerDao;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;

@Service
public class RedisMonitorServerService {
	
	@Autowired
	private MonitorServerDao monitorServerDao;
	
	public MonitorServer add(MonitorServer server){
		MonitorServer monitorServer = monitorServerDao.getByNode(server.getRedisNodeId());
		if(monitorServer!=null){
			return monitorServer;
		}
		int add = monitorServerDao.add(server);
		return add>0?server:null;
	}
	

}
