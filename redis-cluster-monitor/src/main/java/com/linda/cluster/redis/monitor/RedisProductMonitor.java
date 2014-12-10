package com.linda.cluster.redis.monitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;
import com.linda.cluster.redis.monitor.service.RedisClusterAdminService;
import com.linda.cluster.redis.monitor.service.RedisInfoDataService;

public class RedisProductMonitor extends Thread{
	
	private AtomicBoolean stop = new AtomicBoolean(false);
	
	private long interval = 10000;//默认10s
	
	private Product product;
	
	private RedisInfoDataService redisInfoDataService;
	
	private RedisClusterAdminService redisClusterAdminService;
	
	private Map<RedisNode,Jedis> connectionMap =  new HashMap<RedisNode,Jedis>();
	
	private Map<RedisNode,Cluster> nodeClusterMap =  new HashMap<RedisNode,Cluster>();
	
	private Logger logger = Logger.getLogger(RedisProductMonitor.class);
	
	public RedisProductMonitor(Product product,RedisInfoDataService redisInfoDataService,RedisClusterAdminService redisClusterAdminService){
		this.product = product;
		this.redisInfoDataService = redisInfoDataService;
		this.redisClusterAdminService = redisClusterAdminService;
		List<Cluster> clusters = product.getClusters();
		if(clusters!=null){
			for(Cluster cluster:clusters){
				List<RedisNode> nodes = cluster.getNodes();
				for(RedisNode node:nodes){
					nodeClusterMap.put(node, cluster);
					connectionMap.put(node, new Jedis(node.getHost(),node.getPort()));
				}
			}
		}
	}
	
	@Override
	public void run() {
		while(!stop.get()){
			Set<RedisNode> nodes = connectionMap.keySet();
			for(RedisNode node:nodes){
				Cluster cluster = nodeClusterMap.get(node);
				Jedis jedis = connectionMap.get(node);
				this.info(product, cluster, node,jedis);
			}
			boolean break1 = this.sleepBreak();
			if(break1){
				break;
			}
		}
		Collection<Jedis> js = connectionMap.values();
		for(Jedis j:js){
			j.close();
		}
	}
	
	public void startMonitor(){
		this.start();
	}
	
	public void stopMonitor(){
		stop.set(false);
	}
	
	private boolean sleepBreak(){
		try {
			Thread.currentThread().sleep(interval);
			return false;
		} catch (InterruptedException e) {
			return true;
		}
	}
	
	private void info(Product product,Cluster cluster,RedisNode node,Jedis jedis){
		String info = null;
		try{
			info = jedis.info();
		}catch(Exception e){
			logger.error("jedis info exception "+e.getClass()+""+e.getMessage()+" "+jedis.getClient().getHost()+":"+jedis.getClient().getPort());
			this.handleJedisConnectionLoss(product, node, jedis, 0);
		}
		try{
			if(info!=null){
				RedisMonitorInfoBean infoBean = redisInfoDataService.toInfoBean(product, cluster, node, info);
				redisInfoDataService.alarmAndSaveInfo(infoBean);
			}
		}catch(Exception e){
			logger.error("alarmAndSave error:"+e.getMessage());
		}
	}
	
	private void handleJedisConnectionLoss(Product product,RedisNode node,Jedis jedis,int count){
		try{
			jedis.close();
		}catch(Exception e){
			logger.error("connection close error:"+jedis.getClient().getHost()+":"+jedis.getClient().getPort());
		}
		try{
			logger.info("start to reconnect:"+jedis.getClient().getHost()+":"+jedis.getClient().getPort());
			jedis.connect();
		}catch(Exception e){
			logger.error("connection lost:"+jedis.getClient().getHost()+":"+jedis.getClient().getPort());
			if(count<5){
				this.handleJedisConnectionLoss(product,node,jedis, count+1);
			}else{
				RedisNode node2 = redisClusterAdminService.getNodeById(node.getId());
				if(node2==null){
					logger.error("redis node:"+jedis.getClient().getHost()+":"+jedis.getClient().getPort()+" has been delete");
					nodeClusterMap.remove(node);
					connectionMap.remove(node);
				}
			}
		}
	}

}
