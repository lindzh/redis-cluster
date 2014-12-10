package com.linda.cluster.redis.monitor.pojo;

import java.util.List;

import lombok.Data;

@Data
public class MonitorReplication extends MonitorPartBase {
	
	private long id;
	
	private String role;
	
	private int connected_slaves;
	
	private List<MonitorSlaveBean> slaves;
	
	private long master_repl_offset;
	
	private int repl_backlog_active;
	
	private long repl_backlog_size;
	
	private long repl_backlog_first_byte_offset;
	
	private long repl_backlog_histlen;
	
	public void addSlave(MonitorSlaveBean slave){
		slaves.add(slave);
	}
	
	
}
