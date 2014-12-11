package com.linda.cluster.redis.monitor.start;

import org.apache.log4j.Logger;

import com.linda.cluster.redis.monitor.RedisProductMonitor;
import com.linda.cluster.redis.monitor.pojo.Product;

public class RedisProductMonitorBootStrap extends AbstractMonitorBootStrap {
	private static Logger logger = Logger.getLogger(RedisProductMonitorBootStrap.class);
	
	public RedisProductMonitor newProductMonitor(long productId){
		Product product = redisClusterAdminService.getProduct(productId, true);
		if(product==null){
			throw new IllegalArgumentException("product can't be null");
		}
		return new RedisProductMonitor(product,redisInfoDataService,redisClusterAdminService);
	}
	
	public static void main(String[] args) {
		long productId = -1;
		if(args!=null){
			for(String arg:args){
				if(arg.startsWith("-p")){
					productId = parseLong(getValue(arg,"-p"),productId);
				}
			}
		}
		if(productId<0){
			throw new IllegalArgumentException("invalid product parameter");
		}
		RedisProductMonitorBootStrap strap = new RedisProductMonitorBootStrap();
		strap.startup();
		logger.info("resource inject success");
		RedisProductMonitor monitor = strap.newProductMonitor(productId);
		monitor.startup();
		logger.info("monitor startup productId:"+productId);
	}
}
