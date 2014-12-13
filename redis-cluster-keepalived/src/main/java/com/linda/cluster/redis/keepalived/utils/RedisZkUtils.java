package com.linda.cluster.redis.keepalived.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.bean.KeyValueBean;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.common.utils.KeyValueUtils;

public class RedisZkUtils {
	
	public static String toString(List<HostAndPort> hosts){
		int size = hosts.size();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(HostAndPort host:hosts){
			sb.append(host.getHost());
			sb.append(":");
			sb.append(host.getPort());
			if(index<size-1){
				sb.append(",");
			}
			index++;
		}
		return sb.toString();
	}

	public static String toString(HostAndPort hostAndPort){
		return JSONUtils.toJson(hostAndPort);
	}
	
	public static List<HostAndPort> filterSlaves(Collection<HostAndPort> replicationSlaves,Collection<HostAndPort> monitorNodes){
		List<HostAndPort> list = new ArrayList<HostAndPort>();
		for(HostAndPort monitorNode:monitorNodes){
			for(HostAndPort replication:replicationSlaves){
				if(monitorNode.getHost().equals(replication.getHost())&&monitorNode.getPort()==replication.getPort()){
					list.add(monitorNode);
				}
			}
		}
		return list;
	}
	
	public static List<HostAndPort> getSlaves(String info){
		ArrayList<HostAndPort> list = new ArrayList<HostAndPort>();
		Properties properties = new Properties();
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(info.getBytes())));
			String line = reader.readLine();
			boolean started = false;
			while(line!=null){
				line = line.trim();
				if(line.length()<1){
					started = false;
				}
				if(line.startsWith("#")&&line.contains("Replication")){
					started = true;
				}
				if(started){
					KeyValueBean kv = KeyValueUtils.toKeyValue(line, ":");
					properties.setProperty(kv.getKey(), kv.getValue());
				}
			}
			reader.close();
		}catch(Exception e){
			
		}
		String slaves = properties.getProperty("connected_slaves");
		if(slaves!=null){
			int count = Integer.parseInt(slaves);
			for(int i=0;i<count;i++){
				String key = "slave"+i;
				String slaveInfo = properties.getProperty(key);
				if(slaveInfo!=null){
					List<KeyValueBean> kvs = KeyValueUtils.toKeyValue(slaveInfo, "=", ",");
					Properties slaveInfoProp = KeyValueUtils.toProperties(kvs);
					if(slaveInfoProp!=null){
						String ip = slaveInfoProp.getProperty("ip");
						String port = slaveInfoProp.getProperty("port");
						if(ip!=null&&port!=null){
							HostAndPort hostAndPort = new HostAndPort();
							hostAndPort.setHost(ip);
							hostAndPort.setPort(Integer.parseInt(port));
							list.add(hostAndPort);
						}
					}
				}
			}
		}
		return list;
	}
}
