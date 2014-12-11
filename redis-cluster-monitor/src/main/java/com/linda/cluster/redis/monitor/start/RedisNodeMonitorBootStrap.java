package com.linda.cluster.redis.monitor.start;

import org.apache.log4j.Logger;

import com.linda.cluster.redis.monitor.RedisNodeMonitor;

public class RedisNodeMonitorBootStrap extends AbstractMonitorBootStrap{

	private static Logger logger = Logger.getLogger(RedisNodeMonitorBootStrap.class);
	
	public RedisNodeMonitor newNodeMonitor(long productId,long clusterId,long nodeId){
		RedisNodeMonitor monitor = new RedisNodeMonitor(productId,clusterId,nodeId);
		monitor.setRedisClusterAdminService(redisClusterAdminService);
		monitor.setRedisInfoDataService(redisInfoDataService);
		return monitor;
	}
	
	public static void main(String[] args) {
		long productId = -1;
		long clusterId = -1;
		long nodeId = -1;
		if(args!=null){
			for(String arg:args){
				if(arg.startsWith("-p")){
					productId = parseLong(getValue(arg,"-p"),productId);
				}else if(arg.startsWith("-c")){
					clusterId = parseLong(getValue(arg,"-c"),clusterId);
				}else if(arg.startsWith("-n")){
					nodeId = parseLong(getValue(arg,"-n"),nodeId);
				}
			}
		}
		if(productId<0||clusterId<0||nodeId<0){
			throw new IllegalArgumentException("invalid product cluster node parameter");
		}
		RedisNodeMonitorBootStrap strap = new RedisNodeMonitorBootStrap();
		strap.startup();
		logger.info("resource inject success");
		RedisNodeMonitor monitor = strap.newNodeMonitor(productId, clusterId, nodeId);
		monitor.startup();
		logger.info("monitor startup productId:"+productId+" clusterId:"+clusterId+" nodeId:"+nodeId);
	}
}
