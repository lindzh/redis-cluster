package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorPersistence extends MonitorPartBase {
	private long id;
	
	private long rdb_changes_since_last_save;
	
	private int rdb_bgsave_in_progress;
	
	private long rdb_last_save_time;
	
	private String rdb_last_bgsave_status;
	
	private long rdb_last_bgsave_time_sec;
	
	private long rdb_current_bgsave_time_sec;
	
	private int aof_enabled;
	
	private int aof_rewrite_in_progress;
	
	private int aof_rewrite_scheduled;

	private long aof_last_rewrite_time_sec;
	
	private long aof_current_rewrite_time_sec;
	
	private String aof_last_bgrewrite_status;
	
	private String aof_last_write_status;
}
