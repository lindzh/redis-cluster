package com.linda.cluster.redis.monitor;

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
import com.linda.cluster.redis.monitor.pojo.MonitorClients;
import com.linda.cluster.redis.monitor.pojo.MonitorCpu;
import com.linda.cluster.redis.monitor.pojo.MonitorKeyspace;
import com.linda.cluster.redis.monitor.pojo.MonitorMemory;
import com.linda.cluster.redis.monitor.pojo.MonitorPersitence;
import com.linda.cluster.redis.monitor.pojo.MonitorReplication;
import com.linda.cluster.redis.monitor.pojo.MonitorServer;
import com.linda.cluster.redis.monitor.pojo.MonitorSlaveBean;
import com.linda.cluster.redis.monitor.pojo.MonitorStat;

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
	
	public static Properties toProperties(List<KeyValueBean> keyvalues){
		if(keyvalues!=null&&keyvalues.size()>0){
			Properties props = new Properties();
			for(KeyValueBean keyvalue:keyvalues){
				props.setProperty(keyvalue.getKey(), keyvalue.getValue());
			}
			return props;
		}
		return null;
	}
	
	public static MonitorServer toServerBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorServer.class, properties);
	}
	
	public static MonitorClients toClientsBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorClients.class, properties);
	}

	public static MonitorMemory toMemoryBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorMemory.class, properties);
	}
	
	public static MonitorCpu toCpuBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorCpu.class, properties);
	}
	
	public static MonitorStat toStatBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorStat.class, properties);
	}
	
	public static MonitorPersitence toPersistenceBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
		return IntrospectorUtils.getInstance(MonitorPersitence.class, properties);
	}
	
	public static MonitorReplication toReplicationBean(List<KeyValueBean> keyvalues){
		Properties properties = RedisMonitorUtils.toProperties(keyvalues);
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
					Properties slaveProperties = RedisMonitorUtils.toProperties(list);
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
						Properties properties = RedisMonitorUtils.toProperties(values);
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
