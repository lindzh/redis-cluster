package com.linda.cluster.redis.common.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.annotate.JsonIgnore;

@AllArgsConstructor
@NoArgsConstructor
public class HostAndPort {
	
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private String host;
	@Getter
	@Setter
	private int port;
	@Getter
	@Setter
	private boolean alive;//节点是否存活，用于keepalived info
	@Getter
	@Setter
	private String master;
	@Getter
	@Setter
	@JsonIgnore
	private HostAndPort next;
	@Getter
	@Setter
	@JsonIgnore
	private Stat stat;
	
	public HostAndPort(String host,int port){
		this.host = host;
		this.port = port;
	}

	public Object clone() throws CloneNotSupportedException {
		HostAndPort hostAndPort = new HostAndPort();
		hostAndPort.name = name;
		hostAndPort.host = host;
		hostAndPort.port = port;
		hostAndPort.alive = alive;
		hostAndPort.master = master;
		hostAndPort.stat = stat;
		hostAndPort.next = null;
		return hostAndPort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (alive ? 1231 : 1237);
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + port;
		return result;
	}
	
	public void copyFileds(HostAndPort hh){
		this.name = hh.name;
		this.master = hh.master;
		this.host = hh.host;
		this.port = hh.port;
		this.alive = hh.alive;
		this.stat = hh.stat;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HostAndPort other = (HostAndPort) obj;
		if (alive != other.alive)
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
}
