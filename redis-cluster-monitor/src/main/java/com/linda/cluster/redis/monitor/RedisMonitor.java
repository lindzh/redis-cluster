package com.linda.cluster.redis.monitor;

import java.util.List;

import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.monitor.pojo.MonitorClients;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;
import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;
import com.linda.cluster.redis.monitor.pojo.MonitorPersitence;
import com.linda.cluster.redis.monitor.pojo.MonitorReplication;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;
import com.linda.cluster.redis.monitor.pojo.MonitorStat;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class RedisMonitor {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		
		HostAndPort redisHostAndPort = new HostAndPort("yixin12.server.163.org", 22121);
		Jedis jedis = new Jedis(redisHostAndPort.getHost(),redisHostAndPort.getPort());
		jedis.connect();
		String info = jedis.info();
		System.out.println(info);
		System.out.println();
		System.out.println();
		RedisMonitorBean bean = RedisMonitorUtils.toInfoMonitorBean(info);
		MonitorServer server = RedisMonitorUtils.toServerBean(bean.getServerProperties());
		MonitorClients client = RedisMonitorUtils.toClientsBean(bean.getClientProperties());
		MonitorCpu cpu = RedisMonitorUtils.toCpuBean(bean.getCpuProperties());
		MonitorMemory memory = RedisMonitorUtils.toMemoryBean(bean.getMemoryProperties());
		List<MonitorKeyspace> keySpace = RedisMonitorUtils.toKeySpace(bean.getKeyspaceProperties());
		MonitorPersitence persitence = RedisMonitorUtils.toPersistenceBean(bean.getPersistentProperties());
		MonitorReplication replication = RedisMonitorUtils.toReplicationBean(bean.getReplicationProperties());
		MonitorStat stat = RedisMonitorUtils.toStatBean(bean.getStatProperties());		
		
		System.out.println(JSONUtils.toJson(server));
		System.out.println(JSONUtils.toJson(client));
		System.out.println(JSONUtils.toJson(cpu));
		System.out.println(JSONUtils.toJson(memory));
		System.out.println(JSONUtils.toJson(keySpace));
		System.out.println(JSONUtils.toJson(persitence));
		System.out.println(JSONUtils.toJson(replication));
		System.out.println(JSONUtils.toJson(stat));
		jedis.close();
	}
}
