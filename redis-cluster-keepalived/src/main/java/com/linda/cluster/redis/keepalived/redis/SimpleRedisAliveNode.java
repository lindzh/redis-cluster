package com.linda.cluster.redis.keepalived.redis;

import java.util.ArrayList;
import java.util.List;

import com.linda.cluster.redis.common.bean.HostAndPort;

public class SimpleRedisAliveNode extends RedisAliveBase{

	public boolean slaveOf(HostAndPort hostAndPort){
		try{
			if(hostAndPort!=null){
				jedis.slaveof(hostAndPort.getHost(), hostAndPort.getPort());
			}else{
				jedis.slaveofNoOne();
			}
			return true;
		}catch(Exception e){
			super.onException(this, e);
		}
		return false;
	}
		
	public List<HostAndPort> getSlaves(){
		List<HostAndPort> slaves = new ArrayList<HostAndPort>();
		try{
			String info = jedis.info();
			//TODO
		}catch(Exception e){
			super.onException(this, e);
		}
		return slaves;
	}
	
}
