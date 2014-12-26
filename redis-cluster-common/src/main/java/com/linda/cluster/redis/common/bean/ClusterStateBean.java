package com.linda.cluster.redis.common.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.zookeeper.data.Stat;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.omg.PortableServer.POAManagerPackage.State;

@AllArgsConstructor
@NoArgsConstructor
public class ClusterStateBean {
	
	//集群是否存活
	@Getter
	@Setter
	private boolean alive;
	@Getter
	@Setter
	private String master;
	@Getter
	@Setter
	@JsonIgnore
	private Stat stat;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (alive ? 1231 : 1237);
		result = prime * result + ((master == null) ? 0 : master.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterStateBean other = (ClusterStateBean) obj;
		if (alive != other.alive)
			return false;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		return true;
	}
	
	public static ClusterStateBean copyState(ClusterStateBean bean) {
		if(bean!=null){
			ClusterStateBean stateBean = new ClusterStateBean();
			stateBean.alive = bean.alive;
			stateBean.master = bean.master;
			stateBean.stat = bean.stat;
			return stateBean;
		}
		return null;
	}
}
