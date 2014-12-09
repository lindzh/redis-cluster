package com.linda.cluster.redis.monitor;

import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.monitor.pojo.MonitorClients;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;

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
		System.out.println(JSONUtils.toJson(server));
		System.out.println(JSONUtils.toJson(client));
		System.out.println(JSONUtils.toJson(cpu));
		System.out.println(JSONUtils.toJson(memory));
		jedis.close();
	}
}
