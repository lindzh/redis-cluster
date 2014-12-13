			package com.linda.cluster.redis.keepalived.redis;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.bean.HostAndPort;

public class MultiRedisAlivedPingService implements Service,Runnable{

	@Getter
	@Setter
	private int interval = 5000;
	private Thread thread;
	
	private AtomicBoolean stop = new AtomicBoolean(false);
	
	private CopyOnWriteArrayList<SimpleRedisAliveNode> redisNodes = new CopyOnWriteArrayList<SimpleRedisAliveNode>();
	
	private Logger logger = Logger.getLogger(MultiRedisAlivedPingService.class);
	
	public void addRedisNode(SimpleRedisAliveNode node){
		this.init(node);
		redisNodes.add(node);
	}
	
	public SimpleRedisAliveNode getByHostAndPort(HostAndPort hostAndPort){
		for(SimpleRedisAliveNode redisNode:redisNodes){
			if(redisNode.getRedisHost()==hostAndPort){
				return redisNode;
			}
		}
		return null;
	}
	
	public void remodeNode(SimpleRedisAliveNode node){
		redisNodes.remove(node);
	}
	
	@Override
	public void startup() {
		thread = new Thread(this);
		thread.start();
	}

	private void init(RedisAliveBase node){
		node.connect();
	}
	
	@Override
	public void shutdown() {
		stop.set(true);
		thread.interrupt();
		for(RedisAliveBase node:redisNodes){
			node.close();
		}
	}

	@Override
	public void run() {
		while(!stop.get()){
			for(RedisAliveBase node:redisNodes){
				try{
					node.ping();
				}catch(Exception e){
					logger.error(e.getClass()+" "+e.getMessage()+" node:"+node.getRedisHost().getHost()+":"+node.getRedisHost().getPort());
				}
			}
			try {
				Thread.currentThread().sleep(interval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
