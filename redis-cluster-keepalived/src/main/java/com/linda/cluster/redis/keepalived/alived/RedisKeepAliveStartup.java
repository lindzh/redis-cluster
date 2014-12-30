package com.linda.cluster.redis.keepalived.alived;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;
import com.linda.cluster.redis.keepalived.conf.RedisProduct;
import com.linda.cluster.redis.keepalived.conf.RedisZkConf;
import com.linda.cluster.redis.keepalived.zk.RedisZkClusterAliveService;

public class RedisKeepAliveStartup {
	
	private static Logger logger = Logger.getLogger(RedisKeepAliveStartup.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String file = "classpath:keepalived-conf.json";
		InputStream ins = IOUtils.getClassPathInputStream(file);
		if(ins==null){
			logger.info("conf file:"+file+" can't find");
			System.exit(0);
		}
		String confJson = IOUtils.toString(ins);
		ins.close();
		RedisZkConf zkConf = JSONUtils.fromJson(confJson, RedisZkConf.class);
		if(zkConf==null){
			logger.info("load conf file:"+file+" error");
			System.exit(0);
		}else{
			String base = zkConf.getZkBasePath();
			List<RedisProduct> products = zkConf.getProducts();
			List<HostAndPort> zkhosts = zkConf.getZkhosts();
			String server = RedisZookeeperUtils.toString(zkhosts);	
			Object sync = new Object();
			logger.info("start to connect to zk service");
			ZooKeeper keeper = new ZooKeeper(server,10000,new AlivedWatcher(sync));
			logger.info("wait for zk connect");
			synchronized(sync){
				sync.wait();
			}
			logger.info("zk connected");
			ArrayList<RedisZkClusterAliveService> clusterMonitors = new ArrayList<RedisZkClusterAliveService>();
			if(products!=null&&products.size()>0){
				for(RedisProduct product:products){
					List<String> clusters = product.getClusters();
					for(String cluster:clusters){
						RedisZkClusterAliveService clusterAlive = new RedisZkClusterAliveService(keeper,base,product.getProductName(),cluster,product.getPingInterval());
						clusterMonitors.add(clusterAlive);
					}
				}
			}
			if(clusterMonitors.size()>0){
				logger.info("cluster monitor cluster size:"+clusterMonitors.size());
				for(RedisZkClusterAliveService alive:clusterMonitors){
					alive.startup();
				}
				logger.info("start up monitor success");
			}else{
				logger.info("cluster monitor clusters 0 start to exit");
				keeper.close();
			}
		}
	}

}
