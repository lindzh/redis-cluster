package com.linda.cluster.redis.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;
import com.linda.cluster.redis.monitor.service.RedisClusterAdminService;
import com.linda.cluster.redis.monitor.service.RedisInfoDataService;

public class RedisNodeMonitor implements Runnable,Service{

	private long productId;
	private long clusterId;
	private long nodeId;
	
	private Product product;
	private Cluster cluster;
	private RedisNode redisNode;
	private Jedis jedis;
	
	private Thread monitorThread;
	
	@Setter
	private RedisClusterAdminService redisClusterAdminService;
	@Setter
	private RedisInfoDataService redisInfoDataService;
	
	private AtomicBoolean stop = new AtomicBoolean(false);
	
	private long interval = 10000;//默认10s
	
	private Logger logger = Logger.getLogger(RedisNodeMonitor.class);
	
	public RedisNodeMonitor(long productId,long clusterId,long nodeId){
		this.productId = productId;
		this.clusterId = clusterId;
		this.nodeId = nodeId;
	}
	
	private void init(){
		this.product = redisClusterAdminService.getProduct(productId, false);
		this.cluster = redisClusterAdminService.getCluster(clusterId, false);
		this.redisNode = redisClusterAdminService.getNodeById(nodeId);
		if(product==null||cluster==null||redisNode==null){
			throw new IllegalArgumentException("resource not exist:productId "+productId+" clusterId:"+clusterId+" nodeId:"+nodeId);
		}
		jedis = new Jedis(redisNode.getHost(),redisNode.getPort());
	}
	
	@Override
	public void run() {
		while(!stop.get()){
			this.info(product, cluster, redisNode,jedis);
			boolean break1 = this.sleepBreak();
			if(break1){
				break;
			}
		}
		jedis.close();
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
				}
			}
		}
	}
	
	@Override
	public void startup() {
		this.init();
		monitorThread = new Thread(this);
		monitorThread.start();
	}

	@Override
	public void shutdown() {
		monitorThread.interrupt();
		stop.set(true);
	}
	
}
