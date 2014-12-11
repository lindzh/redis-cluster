package com.linda.cluster.redis.monitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorReplicationDao;
import com.linda.cluster.redis.monitor.pojo.MonitorReplication;

@Service
public class RedisMonitorReplicationService {
	
	@Autowired
	private MonitorReplicationDao monitorReplicationDao;
	
	public MonitorReplication add(MonitorReplication replication){
		int add = monitorReplicationDao.add(replication);
		return add>0?replication:null;
	}
	

}
