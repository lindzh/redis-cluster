package com.linda.cluster.redis.monitor;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;
import com.linda.cluster.redis.monitor.service.RedisClusterAdminService;
import com.linda.cluster.redis.monitor.service.RedisInfoDataService;

@Data
public class RedisMonitor {
	
	private RedisInfoDataService infoDataService;
	
	private RedisClusterAdminService redisClusterAdminService;
	
	private Logger logger = Logger.getLogger(RedisMonitor.class);
	
	private ConcurrentHashMap<Product, RedisProductMonitor> productMonitorMap = new ConcurrentHashMap<Product, RedisProductMonitor>();
	
	private Product getProduct(long id){
		Set<Product> products = productMonitorMap.keySet();
		for(Product product:products){
			if(product.getId()==id){
				return product;
			}
		}
		return null;
	}
	
	public void infoNode(Jedis jedis,Product product,Cluster cluster,RedisNode node){
		String info = jedis.info();
		if(info!=null){
			RedisMonitorInfoBean bean = infoDataService.toInfoBean(product, cluster, node, info);
			infoDataService.alarmAndSaveInfo(bean);
		}
	}
	
	public void startMonitor(){
		List<Product> products = redisClusterAdminService.getAllProducts(true);
		if(products!=null&&products.size()>0){
			logger.info("monitor start "+products.size()+" threads to monitor "+products.size()+" product");
			for(Product pdt:products){
				RedisProductMonitor monitor = new RedisProductMonitor(pdt,infoDataService,redisClusterAdminService);
				productMonitorMap.put(pdt, monitor);
				monitor.startMonitor();
			}
		}else{
			logger.info("monitor no product has in database");
		}
	}
	
	public void stopMonitor(){
		Collection<RedisProductMonitor> monitors = productMonitorMap.values();
		for(RedisProductMonitor monitor:monitors){
			monitor.stopMonitor();
		}
	}
	
	public void restartProductMonitor(long productId){
		this.stopProductMonitor(productId);
		Product product = redisClusterAdminService.getProduct(productId, true);
		RedisProductMonitor monitor = new RedisProductMonitor(product,infoDataService,redisClusterAdminService);
		productMonitorMap.put(product, monitor);
		monitor.startMonitor();
	}
	
	public void stopProductMonitor(long productId){
		Product product = this.getProduct(productId);
		if(product!=null){
			RedisProductMonitor monitor = productMonitorMap.get(product);
			monitor.stopMonitor();
			monitor.interrupt();
			productMonitorMap.remove(product);
		}
	}
}
