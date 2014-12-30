package com.linda.cluster.redis.config.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.bean.ClusterStateBean;
import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.common.bean.RedisProductDataBean;
import com.linda.cluster.redis.common.sharding.Sharding;
import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.config.config.RedisZkConfig;

public class RedisAdminYixin12Service {
	
	private static Logger logger = Logger.getLogger(RedisAdminYixin12Service.class);
	
	public static void main(String[] args) {
		
		Watcher watcher = new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				
			}
		};
		
		int timeout = 10000;
		InputStream ins = IOUtils.getClassPathInputStream("redis-zookeeper-conf.json");
		String conf = IOUtils.toString(ins);
		RedisZkConfig zkConfig = JSONUtils.fromJson(conf, RedisZkConfig.class);
		String server = zkConfig.toZkServer();
		try {
			ZooKeeper zooKeeper = new ZooKeeper(server, timeout, watcher);
			RedisAdminService adminService = new RedisAdminService();
			adminService.setZkConfig(zkConfig);
			adminService.setZookeeper(zooKeeper);
			RedisProductDataBean productData = new RedisProductDataBean();
			productData.setId(4);
			ArrayList<Sharding> shardings = new ArrayList<Sharding>();
			Sharding shard1 = new Sharding();
			shard1.setFrom(0);
			shard1.setTo(127);
			shard1.setNode("yixin12-cluster1");
			Sharding shard2 = new Sharding();
			shard2.setFrom(128);
			shard2.setTo(255);
			shard2.setNode("yixin12-cluster2");
			shardings.add(shard1);
			shardings.add(shard2);
			productData.setSharding(shardings);
			productData.setBackup(null);
			boolean addProductResult = adminService.addOrUpdateProduct("yixin12", productData);
			logger.info("add product yixin12 result:"+addProductResult);
			
			//================================cluster1======================================================
			ClusterStateBean cluster1State = new ClusterStateBean();
			cluster1State.setAlive(false);
			cluster1State.setMaster("node-12301");
			boolean addCluster1 = adminService.addOrUpdateCluster("yixin12", "yixin12-cluster1", cluster1State);
			logger.info("add cluster yixin12 yixin12-cluster1 result:"+addCluster1);
			
			HostAndPort cluster1Node1 = new HostAndPort();
			cluster1Node1.setAlive(false);
			cluster1Node1.setHost("10.120.151.105");
			cluster1Node1.setPort(12301);
			cluster1Node1.setName("node-12301");
			boolean cluster1Node1Result = adminService.addOrUpdateRedisNode("yixin12", "yixin12-cluster1", "node-12301", cluster1Node1);
			logger.info("add redis node node-12301 result:"+cluster1Node1Result);
			
			HostAndPort cluster1Node2 = new HostAndPort();
			cluster1Node2.setAlive(false);
			cluster1Node2.setHost("10.120.151.105");
			cluster1Node2.setPort(12302);
			cluster1Node2.setName("node-12302");
			cluster1Node2.setMaster("node-12301");
			boolean cluster1Node2Result = adminService.addOrUpdateRedisNode("yixin12", "yixin12-cluster1", "node-12302", cluster1Node2);
			logger.info("add redis node cluster1-node2 result:"+cluster1Node2Result);
			
			HostAndPort cluster1Node3 = new HostAndPort();
			cluster1Node3.setAlive(false);
			cluster1Node3.setHost("10.120.151.105");
			cluster1Node3.setPort(12303);
			cluster1Node3.setName("node-12303");
			cluster1Node3.setMaster("node-12302");
			boolean cluster1Node3Result = adminService.addOrUpdateRedisNode("yixin12", "yixin12-cluster1", "node-12303", cluster1Node3);
			logger.info("add redis node cluster1-node3 result:"+cluster1Node3Result);
			
			//==================================end cluster1=====================================================
			
			ClusterStateBean cluster2State = new ClusterStateBean();
			cluster2State.setAlive(false);
			cluster2State.setMaster("node-12401");
			boolean addCluster2 = adminService.addOrUpdateCluster("yixin12", "yixin12-cluster2", cluster1State);
			logger.info("add cluster yixin12 yixin12-cluster2 result:"+addCluster2);
			
			HostAndPort cluster2Node1 = new HostAndPort();
			cluster2Node1.setAlive(false);
			cluster2Node1.setHost("10.120.151.105");
			cluster2Node1.setPort(12401);
			cluster2Node1.setName("node-12401");
			boolean cluster2Node1Result = adminService.addOrUpdateRedisNode("yixin12", "yixin12-cluster2", "node-12401", cluster2Node1);
			logger.info("add redis node node-12301 result:"+cluster2Node1Result);
			
			HostAndPort cluster2Node2 = new HostAndPort();
			cluster2Node2.setAlive(false);
			cluster2Node2.setHost("10.120.151.105");
			cluster2Node2.setPort(12402);
			cluster2Node2.setName("node-12402");
			cluster2Node2.setMaster("node-12401");
			boolean cluster2Node2Result = adminService.addOrUpdateRedisNode("yixin12", "yixin12-cluster2", "node-12402", cluster2Node2);
			logger.info("add redis node cluster1-node2 result:"+cluster2Node2Result);
			
			HostAndPort cluster2Node3 = new HostAndPort();
			cluster2Node3.setAlive(false);
			cluster2Node3.setHost("10.120.151.105");
			cluster2Node3.setPort(12403);
			cluster2Node3.setName("node-12403");
			cluster2Node3.setMaster("node-12402");
			boolean cluster2Node3Result = adminService.addOrUpdateRedisNode("yixin12", "yixin12-cluster2", "node-12403", cluster2Node3);
			logger.info("add redis node cluster1-node3 result:"+cluster2Node3Result);
			
			logger.info("create products cluster nodes success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
