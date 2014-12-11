package com.linda.cluster.redis.monitor;

import java.util.List;

import lombok.Data;

import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.MonitorClients;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;
import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;
import com.linda.cluster.redis.monitor.pojo.MonitorPersistence;
import com.linda.cluster.redis.monitor.pojo.MonitorReplication;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;
import com.linda.cluster.redis.monitor.pojo.MonitorStat;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;

@Data
public class RedisMonitorInfoBean {
	private Product product;
	private Cluster cluster;
	private RedisNode node;
	private MonitorClients clients;
	private MonitorCpu cpu;
	private List<MonitorKeyspace> keyspaces;
	private MonitorMemory memory;
	private MonitorPersistence persistence;
	private MonitorReplication replication;
	private MonitorServer server;
	private MonitorStat stat;
	

}
