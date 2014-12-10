package com.linda.cluster.redis.monitor.pojo;


public class MonitorKeyspace extends MonitorPartBase {
	
	private long id;
	
	private int databaseId;
	
	private int keys;
	private int expires;
	private int avg_ttl;
	
	private int keynum;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	public int getKeys() {
		return keys;
	}

	public void setKeys(int keys) {
		this.keynum = keys;
		this.keys = keys;
	}

	public int getExpires() {
		return expires;
	}

	public void setExpires(int expires) {
		this.expires = expires;
	}

	public int getAvg_ttl() {
		return avg_ttl;
	}

	public void setAvg_ttl(int avg_ttl) {
		this.avg_ttl = avg_ttl;
	}

	public int getKeynum() {
		return keynum;
	}

	public void setKeynum(int keynum) {
		this.keys = keynum;
		this.keynum = keynum;
	}
	
}
