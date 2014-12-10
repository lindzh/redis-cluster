package com.linda.cluster.redis.monitor.service;

import lombok.Data;
import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.monitor.RedisMonitorInfoBean;
import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;

@Data
public class RedisNodeMonitor {
	
	private RedisInfoDataService infoDataService;
	
	public void infoNode(Jedis jedis,Product product,Cluster cluster,RedisNode node){
		String info = jedis.info();
		if(info!=null){
			RedisMonitorInfoBean bean = infoDataService.toInfoBean(product, cluster, node, info);
			infoDataService.alarmAndSaveInfo(bean);
		}
	}
}
