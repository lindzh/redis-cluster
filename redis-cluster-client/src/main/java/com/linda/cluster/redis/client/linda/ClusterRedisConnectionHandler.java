package com.linda.cluster.redis.client.linda;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

public class ClusterRedisConnectionHandler extends JedisSlotBasedConnectionHandler implements Watcher{
	
	//zk service list
	private List<HostAndPort> zkhosts;
	//zk base path
	private String redisBasePath;
	//product config
	private String product;
	//product password
	private String password;
	
	public ClusterRedisConnectionHandler(List<HostAndPort> zkhosts,String product){
		this(zkhosts,product,null,null);
	}
	
	public ClusterRedisConnectionHandler(List<HostAndPort> zkhosts,String product,String password){
		this(zkhosts,product,password,null);
	}
	
	public ClusterRedisConnectionHandler(List<HostAndPort> zkhosts,String product,String password,String basepath){
		this(null);
		this.zkhosts = zkhosts;
		this.product = product;
		this.password = password;
		this.redisBasePath = basepath;
	}
	
	private ClusterRedisConnectionHandler(Set<HostAndPort> nodes) {
		super(Collections.EMPTY_SET);
	}
	
	public void returnConnection(Jedis connection){
		super.returnConnection(connection);
	}
	
	public void close(){
		
	}

	@Override
	public void process(WatchedEvent event) {
		
	}

}
