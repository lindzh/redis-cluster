package com.linda.cluster.redis.keepalived.alived;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.IntrospectorUtils;
import com.linda.cluster.redis.keepalived.conf.RedisProduct;
import com.linda.cluster.redis.keepalived.conf.RedisZkConf;
import com.linda.cluster.redis.keepalived.utils.RedisZkUtils;
import com.linda.cluster.redis.keepalived.zk.RedisZkClusterAliveService;

public class RedisKeepAliveStartup {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String file = "classpath:keepalived-conf.json";
		InputStream ins = IOUtils.getClassPathInputStream(file);
		if(ins==null){
			System.out.println("conf file:"+file+" can't find");
			System.exit(0);
		}
		Properties properties = IOUtils.loadProperties(ins);
		ins.close();
		RedisZkConf zkConf = IntrospectorUtils.getInstance(RedisZkConf.class, properties);
		if(zkConf==null){
			System.out.println("load conf file:"+file+" error");
			System.exit(0);
		}else{
			String base = zkConf.getZkBasePath();
			List<RedisProduct> products = zkConf.getProducts();
			List<HostAndPort> zkhosts = zkConf.getZkhosts();
			String server = RedisZkUtils.toString(zkhosts);	
			ZooKeeper keeper = new ZooKeeper(server,10000,null);
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
				System.out.println("cluster monitor cluster size:"+clusterMonitors.size());
				for(RedisZkClusterAliveService alive:clusterMonitors){
					alive.startup();
				}
				System.out.println("start up monitor success");
			}else{
				System.out.println("cluster monitor clusters 0 start to exit");
				keeper.close();
			}
		}
	}

}
