package com.linda.cluster.redis.monitor.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.linda.cluster.redis.common.bean.KeyValueBean;
import com.linda.cluster.redis.common.utils.IntrospectorUtils;
import com.linda.cluster.redis.common.utils.KeyValueUtils;
import com.linda.cluster.redis.monitor.RedisMonitorBean;
import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.MonitorClients;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;
import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;
import com.linda.cluster.redis.monitor.pojo.MonitorPartBase;
import com.linda.cluster.redis.monitor.pojo.MonitorPersistence;
import com.linda.cluster.redis.monitor.pojo.MonitorReplication;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;
import com.linda.cluster.redis.monitor.pojo.MonitorSlaveBean;
import com.linda.cluster.redis.monitor.pojo.MonitorStat;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;

public class RedisMonitorUtils {
	
	public static final String redisInfoSplitor = ":";
	
	public static RedisMonitorBean toInfoMonitorBean(String info){
		RedisMonitorBean monitorBean = new RedisMonitorBean();
		ByteArrayInputStream bis = new ByteArrayInputStream(info.getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
		try {
			String line = reader.readLine();
			String state = "";
			while(line!=null){
				if(line.startsWith("#")){
					if(line.contains("Server")){
						state = "Server";
					}else if(line.contains("Clients")){
						state = "Clients";
					}else if(line.contains("Memory")){
						state = "Memory";
					}else if(line.contains("Persistence")){
						state = "Persistence";
					}else if(line.contains("Stats")){
						state = "Stats";
					}else if(line.contains("Replication")){
						state = "Replication";
					}else if(line.contains("CPU")){
						state = "CPU";
					}else if(line.contains("Keyspace")){
						state = "Keyspace";
					}
				}else{
					if(line.trim().length()>0){
						KeyValueBean bean = KeyValueUtils.toKeyValue(line, redisInfoSplitor);
						if(bean!=null){
							if(state.equals("Server")){
								monitorBean.addServerKeyValue(bean);
							}else if(state.equals("Clients")){
								monitorBean.addClientKeyValue(bean);
							}else if(state.equals("Memory")){
								monitorBean.addMemoryKeyValue(bean);
							}else if(state.equals("Persistence")){
								monitorBean.addPersistentKeyValue(bean);
							}else if(state.equals("Stats")){
								monitorBean.addStatKeyValue(bean);
							}else if(state.equals("Replication")){
								monitorBean.addReplicationKeyValue(bean);
							}else if(state.equals("CPU")){
								monitorBean.addCpuKeyValue(bean);
							}else if(state.equals("Keyspace")){
								monitorBean.addKeyspaceKeyValue(bean);
							}
						}
					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return monitorBean;
	}
	
	public static void setProductClusterNode(Product product,Cluster cluster,RedisNode node,MonitorPartBase monitorPart){
		if(product!=null){
			monitorPart.setProductId(product.getId());	
		}
		if(cluster!=null){
			monitorPart.setClusterId(cluster.getId());
		}
		if(node!=null){
			monitorPart.setRedisNodeId(node.getId());
		}
	}
	
	public static void setProductClusterNodes(Product product,Cluster cluster,RedisNode node,List<? extends MonitorPartBase> monitorParts){
		for(MonitorPartBase part:monitorParts){
			RedisMonitorUtils.setProductClusterNode(product, cluster, node, part);
		}
	}
	
	public static MonitorServer toServerBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorServer.class, properties);
	}
	
	public static MonitorClients toClientsBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorClients.class, properties);
	}

	public static MonitorMemory toMemoryBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorMemory.class, properties);
	}
	
	public static MonitorCpu toCpuBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorCpu.class, properties);
	}
	
	public static MonitorStat toStatBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorStat.class, properties);
	}
	
	public static MonitorPersistence toPersistenceBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorPersistence.class, properties);
	}
	
	public static MonitorReplication toReplicationBean(List<KeyValueBean> keyvalues){
		Properties properties = KeyValueUtils.toProperties(keyvalues);
		MonitorReplication replication = IntrospectorUtils.getInstance(MonitorReplication.class, properties);
		RedisMonitorUtils.setSlaves(replication, properties);
		return replication;
	}
	
	private static void setSlaves(MonitorReplication replication,Properties properties){
		replication.setSlaves(new ArrayList<MonitorSlaveBean>());
		int slaves = replication.getConnected_slaves();
		int i=0;
		while(i<slaves){
			String slave = properties.getProperty("slave"+i);
			if(slave!=null){
				List<KeyValueBean> list = KeyValueUtils.toKeyValue(slave, "=", ",");
				if(list!=null&&list.size()>0){
					Properties slaveProperties = KeyValueUtils.toProperties(list);
					MonitorSlaveBean bean = IntrospectorUtils.getInstance(MonitorSlaveBean.class, slaveProperties);
					replication.addSlave(bean);
				}
			}
			i++;
		}
	}
	
	private static int getDatabaseId(String name){
		if(name.length()>2){
			String dbId = name.substring(2);
			if(dbId!=null){
				return Integer.parseInt(dbId);
			}
		}
		return -1;
	}
	
	public static List<MonitorKeyspace> toKeySpace(List<KeyValueBean> keyvalues){
		List<MonitorKeyspace> list = new ArrayList<MonitorKeyspace>();
		if(keyvalues!=null&&keyvalues.size()>0){
			for(KeyValueBean kv:keyvalues){
				String db = kv.getKey();
				String va = kv.getValue();
				int databaseId = RedisMonitorUtils.getDatabaseId(db);
				if(databaseId>=0){
					List<KeyValueBean> values = KeyValueUtils.toKeyValue(va, "=", ",");
					if(values!=null&&values.size()>0){
						Properties properties = KeyValueUtils.toProperties(values);
						MonitorKeyspace instance = IntrospectorUtils.getInstance(MonitorKeyspace.class, properties);
						instance.setDatabaseId(databaseId);
						list.add(instance);
					}
				}
			}
		}
		return list;
	}
}
