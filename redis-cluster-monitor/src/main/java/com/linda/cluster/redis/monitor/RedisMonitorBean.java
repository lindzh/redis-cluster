package com.linda.cluster.redis.monitor;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.linda.cluster.redis.common.bean.KeyValueBean;

public class RedisMonitorBean {
	
	@Getter
	private List<KeyValueBean> serverProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> clientProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> memoryProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> persistentProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> statProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> replicationProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> cpuProperties = new ArrayList<KeyValueBean>();
	@Getter
	private List<KeyValueBean> keyspaceProperties = new ArrayList<KeyValueBean>();
	
	public void addServerKeyValue(KeyValueBean bean){
		serverProperties.add(bean);
	}
	
	public void addClientKeyValue(KeyValueBean bean){
		clientProperties.add(bean);
	}
	
	public void addMemoryKeyValue(KeyValueBean bean){
		memoryProperties.add(bean);
	}
	
	public void addPersistentKeyValue(KeyValueBean bean){
		persistentProperties.add(bean);
	}
	
	public void addStatKeyValue(KeyValueBean bean){
		statProperties.add(bean);
	}
	
	public void addReplicationKeyValue(KeyValueBean bean){
		replicationProperties.add(bean);
	}
	
	public void addCpuKeyValue(KeyValueBean bean){
		cpuProperties.add(bean);
	}
	
	public void addKeyspaceKeyValue(KeyValueBean bean){
		keyspaceProperties.add(bean);
	}
}
