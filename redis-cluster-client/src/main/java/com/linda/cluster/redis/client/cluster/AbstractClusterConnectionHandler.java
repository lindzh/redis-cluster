package com.linda.cluster.redis.client.cluster;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.linda.cluster.redis.common.bean.HostAndPort;

public class AbstractClusterConnectionHandler {
	
	private ConcurrentHashMap<Integer,String> slotCache = new ConcurrentHashMap<Integer,String>();
	
	private ConcurrentHashMap<String,String> clusterHostAndPortCache = new ConcurrentHashMap<String,String>();
	
	private ConcurrentHashMap<String,JedisPool> hostAndPortCache = new ConcurrentHashMap<String,JedisPool>();
	
	private JedisPoolConfig poolConfig;
	
	private Logger logger = Logger.getLogger(AbstractClusterConnectionHandler.class);
	
	public AbstractClusterConnectionHandler(JedisPoolConfig poolConfig){
		this.poolConfig = poolConfig;
	}
	
	public Jedis getConnectionFromSlot(int slot){
		String cluster = slotCache.get(slot);
		if(cluster!=null){
			String hostAndPort = clusterHostAndPortCache.get(cluster);
			if(hostAndPort!=null){
				JedisPool pool = hostAndPortCache.get(hostAndPort);
				if(pool!=null){
					return pool.getResource();
				}
			}
		}
		return null;
	}
	
	protected String genHostAndPortKey(String host,int port){
		return host+":"+port;
	}
	
	private JedisPool getPool(Jedis jedis){
		Client client = jedis.getClient();
		String hostAndPortKey = this.genHostAndPortKey(client.getHost(), client.getPort());
		return hostAndPortCache.get(hostAndPortKey);
	}
	
	public void returnConnection(Jedis jedis){
		JedisPool jedisPool = this.getPool(jedis);
		if(jedisPool!=null){
			jedisPool.returnResource(jedis);
		}
	}
	
	public void returnBrokenConnection(Jedis jedis){
		JedisPool jedisPool = this.getPool(jedis);
		if(jedisPool!=null){
			jedisPool.returnBrokenResource(jedis);
		}
	}
	
	protected void close(){
		slotCache.clear();
		clusterHostAndPortCache.clear();
		Collection<JedisPool> pools = hostAndPortCache.values();
		for(JedisPool pool:pools){
			pool.destroy();
		}
		hostAndPortCache.clear();
	}
	
	protected Collection<String> getClusters(){
		return clusterHostAndPortCache.keySet();
	}
	
	protected void changeSharding(int slot,String cluster){
		String hostAndPort = clusterHostAndPortCache.get(cluster);
		if(hostAndPort!=null){
			JedisPool jedisPool = hostAndPortCache.get(hostAndPort);
			if(jedisPool!=null){
				slotCache.put(slot, cluster);
			}else{
				logger.error("can't find jedis pool for cluster "+cluster+" host:"+hostAndPort);
			}
		}else{
			logger.error("can't find host for cluster:"+cluster);
		}
	}
	
	protected void removeCluster(String cluster){
		String hostAndPort = clusterHostAndPortCache.get(cluster);
		if(hostAndPort!=null){
			JedisPool jedisPool = hostAndPortCache.get(hostAndPort);
			if(jedisPool!=null){
				jedisPool.destroy();
			}
			hostAndPortCache.remove(hostAndPort);
		}
		clusterHostAndPortCache.remove(cluster);
	}
	
	protected void changeClusterHostAndPort(String cluster,HostAndPort hostAndPort){
		String hostAndPortStr = clusterHostAndPortCache.get(cluster);
		String newHostAndPortStr = this.genHostAndPortKey(hostAndPort.getHost(), hostAndPort.getPort());
		JedisPool pool = hostAndPortCache.get(newHostAndPortStr);
		if(pool==null){
			pool = new JedisPool(poolConfig,hostAndPort.getHost(),hostAndPort.getPort());
			hostAndPortCache.put(newHostAndPortStr, pool);
		}
		clusterHostAndPortCache.put(cluster, newHostAndPortStr);
	}
}
