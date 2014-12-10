package com.linda.cluster.redis.monitor.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.monitor.RedisMonitorBean;
import com.linda.cluster.redis.monitor.RedisMonitorInfoBean;
import com.linda.cluster.redis.monitor.RedisMonitorUtils;
import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.MonitorClients;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;
import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;
import com.linda.cluster.redis.monitor.pojo.MonitorPersitence;
import com.linda.cluster.redis.monitor.pojo.MonitorReplication;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;
import com.linda.cluster.redis.monitor.pojo.MonitorStat;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;

public class RedisInfoDataService {
	
	private Logger logger = Logger.getLogger(RedisInfoDataService.class);
	
	public RedisMonitorInfoBean toInfoBean(Product product,Cluster cluster,RedisNode node,String info){
		RedisMonitorInfoBean infoBean = new RedisMonitorInfoBean();
		infoBean.setProduct(product);
		infoBean.setCluster(cluster);
		infoBean.setNode(node);
		RedisMonitorBean bean = RedisMonitorUtils.toInfoMonitorBean(info);
		MonitorServer server = RedisMonitorUtils.toServerBean(bean.getServerProperties());
		MonitorClients client = RedisMonitorUtils.toClientsBean(bean.getClientProperties());
		MonitorCpu cpu = RedisMonitorUtils.toCpuBean(bean.getCpuProperties());
		MonitorMemory memory = RedisMonitorUtils.toMemoryBean(bean.getMemoryProperties());
		List<MonitorKeyspace> keySpaces = RedisMonitorUtils.toKeySpace(bean.getKeyspaceProperties());
		MonitorPersitence persistence = RedisMonitorUtils.toPersistenceBean(bean.getPersistentProperties());
		MonitorReplication replication = RedisMonitorUtils.toReplicationBean(bean.getReplicationProperties());
		MonitorStat stat = RedisMonitorUtils.toStatBean(bean.getStatProperties());
		
		infoBean.setServer(server);
		infoBean.setClients(client);
		infoBean.setCpu(cpu);
		infoBean.setMemory(memory);
		infoBean.setKeyspaces(keySpaces);
		infoBean.setStat(stat);
		infoBean.setReplication(replication);
		infoBean.setPersistence(persistence);
		
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, server);
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, client);
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, cpu);
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, memory);
		RedisMonitorUtils.setProductClusterNodes(product, cluster, node, keySpaces);
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, stat);
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, replication);
		RedisMonitorUtils.setProductClusterNode(product, cluster, node, persistence);
		return infoBean;
	}
	
	public void alarmInfo(RedisMonitorInfoBean infoBean){
		logger.info("start to alarm");
	}
	
	public void saveInfo(RedisMonitorInfoBean infoBean){
		logger.info("start to save");
		System.out.println(JSONUtils.toJson(infoBean));
	}
	
	public void alarmAndSaveInfo(RedisMonitorInfoBean infoBean){
		this.alarmInfo(infoBean);
		this.saveInfo(infoBean);
	}
	
}
