package com.linda.cluster.redis.client.push;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisPoolConfig;

import com.linda.cluster.redis.common.bean.RedisZookeeperServiceConfig;
import com.linda.cluster.redis.common.bean.ShardingBean;
import com.linda.cluster.redis.common.spring.BeanInit;


/**
 * jedis cluster client for push platform redis cluster
 * @author hzlindzh
 *
 */
public class JedisClusterService extends JedisCluster implements BeanInit{

    private JedisClusterSlotConnectionHandler myClusterConnectionHandler;
	
	public JedisClusterService(JedisPoolConfig redisPoolConfig,RedisZookeeperServiceConfig config,List<ShardingBean> buckets){
		super(Collections.EMPTY_SET);
		myClusterConnectionHandler = new JedisClusterSlotConnectionHandler(redisPoolConfig,config,buckets);
		this.setSuperConnectionHandler(myClusterConnectionHandler);
	}
	
	private void setSuperConnectionHandler(JedisClusterConnectionHandler handler){
		try {
			Field connectionHandlerField = JedisCluster.class.getDeclaredField("connectionHandler");
			connectionHandlerField.setAccessible(true);
			connectionHandlerField.set(this, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JedisClusterService(Set<HostAndPort> nodes) {
		super(nodes);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		myClusterConnectionHandler.destroy();
	}
}
