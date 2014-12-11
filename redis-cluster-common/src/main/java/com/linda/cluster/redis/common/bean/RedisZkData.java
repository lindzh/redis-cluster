package com.linda.cluster.redis.common.bean;

import lombok.Data;

import org.apache.zookeeper.data.Stat;

@Data
public class RedisZkData {

	private Stat stat = new Stat();
	private byte[] data;

	public long getCzxid() {
		return stat.getCzxid();
	}

	public void setCzxid(long m_) {
		stat.setCzxid(m_);
	}

	public long getMzxid() {
		return stat.getMzxid();
	}

	public void setMzxid(long m_) {
		stat.setMzxid(m_);
	}

	public long getCtime() {
		return stat.getCtime();
	}

	public void setCtime(long m_) {
		stat.setCtime(m_);
	}

	public long getMtime() {
		return stat.getMtime();
	}

	public void setMtime(long m_) {
		stat.setMtime(m_);
	}

	public int getVersion() {
		return stat.getVersion();
	}

	public void setVersion(int m_) {
		stat.setVersion(m_);
	}

	public int getCversion() {
		return stat.getCversion();
	}

	public void setCversion(int m_) {
		stat.setCversion(m_);
	}

	public int getAversion() {
		return stat.getAversion();
	}

	public void setAversion(int m_) {
		stat.setAversion(m_);
	}

	public long getEphemeralOwner() {
		return stat.getEphemeralOwner();
	}

	public void setEphemeralOwner(long m_) {
		stat.setEphemeralOwner(m_);
	}

	public int getDataLength() {
		return stat.getDataLength();
	}

	public void setDataLength(int m_) {
		stat.setDataLength(m_);
	}

	public int getNumChildren() {
		return stat.getNumChildren();
	}

	public void setNumChildren(int m_) {
		stat.setNumChildren(m_);
	}

	public long getPzxid() {
		return stat.getPzxid();
	}

	public void setPzxid(long m_) {
		stat.setPzxid(m_);
	}

}
