package com.linda.cluster.redis.keepalived.redis;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class SimpleRedisAliveNode extends RedisAliveBase{
	
	private Logger logger = Logger.getLogger(SimpleRedisAliveNode.class);

	public boolean slaveOf(HostAndPort hostAndPort){
		try{
			if(hostAndPort!=null){
				logger.info(this.redisHost.getName()+" slaveof "+hostAndPort.getName());
				jedis.slaveof(hostAndPort.getHost(), hostAndPort.getPort());
			}else{
				logger.info(this.redisHost.getName()+" slaveof no one");
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
			return RedisZookeeperUtils.getSlaves(info);
		}catch(Exception e){
			super.onException(this, e);
		}
		return slaves;
	}
	
}
