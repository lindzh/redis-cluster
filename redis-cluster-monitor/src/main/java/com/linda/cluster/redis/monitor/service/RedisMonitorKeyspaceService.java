package com.linda.cluster.redis.monitor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.MonitorKeyspaceDao;
import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;

@Service
public class RedisMonitorKeyspaceService {
	
	@Autowired
	private MonitorKeyspaceDao monitorKeyspaceDao;
	
	public MonitorKeyspace add(List<MonitorKeyspace> keyspaces){
		MonitorKeyspace merge = this.merge(keyspaces);
		if(merge!=null){
			int add = monitorKeyspaceDao.add(merge);
			if(add>0){
				return merge;
			}
		}
		return null;
	}
	
	private MonitorKeyspace merge(List<MonitorKeyspace> keyspaces){
		return keyspaces.get(0);
	}

}
