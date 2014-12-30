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

public class RedisAdminTestService {
	
	private static Logger logger = Logger.getLogger(RedisAdminTestService.class);
	
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
			productData.setId(1);
			ArrayList<Sharding> shardings = new ArrayList<Sharding>();
			Sharding shard1 = new Sharding();
			shard1.setFrom(0);
			shard1.setTo(127);
			shard1.setNode("lindzh-cluster1");
			shardings.add(shard1);
			Sharding shard2 = new Sharding();
			shard2.setFrom(128);
			shard2.setTo(255);
			shard2.setNode("lindzh-cluster2");
			shardings.add(shard2);
			productData.setSharding(shardings);
			productData.setBackup("lindzh-backup");
			boolean addProductResult = adminService.addOrUpdateProduct("lindzh", productData);
			logger.info("add product lindzh result:"+addProductResult);
			
			ClusterStateBean cluster1State = new ClusterStateBean();
			cluster1State.setAlive(false);
			cluster1State.setMaster("cluster1-node1");
			boolean addCluster1 = adminService.addOrUpdateCluster("lindzh", "lindzh-cluster1", cluster1State);
			logger.info("add cluster lindzh lindzh-cluster1 result:"+addCluster1);
			ClusterStateBean cluster2State = new ClusterStateBean();
			cluster2State.setAlive(false);
			cluster2State.setMaster("cluster2-node1");
			boolean addCluster2 = adminService.addOrUpdateCluster("lindzh", "lindzh-cluster2", cluster2State);
			logger.info("add cluster lindzh lindzh-cluster2 result:"+addCluster2);
			
			HostAndPort cluster1Node1 = new HostAndPort();
			cluster1Node1.setAlive(false);
			cluster1Node1.setHost("10.120.151.105");
			cluster1Node1.setPort(12101);
			cluster1Node1.setName("cluster1-node1");
			boolean cluster1Node1Result = adminService.addOrUpdateRedisNode("lindzh", "lindzh-cluster1", "cluster1-node1", cluster1Node1);
			logger.info("add redis node cluster1-node1 result:"+cluster1Node1Result);
			
			HostAndPort cluster1Node2 = new HostAndPort();
			cluster1Node2.setAlive(false);
			cluster1Node2.setHost("10.120.151.105");
			cluster1Node2.setPort(12102);
			cluster1Node2.setName("cluster1-node2");
			cluster1Node2.setMaster("cluster1-node1");
			boolean cluster1Node2Result = adminService.addOrUpdateRedisNode("lindzh", "lindzh-cluster1", "cluster1-node2", cluster1Node2);
			logger.info("add redis node cluster1-node2 result:"+cluster1Node2Result);
			
			HostAndPort cluster2Node1 = new HostAndPort();
			cluster2Node1.setAlive(false);
			cluster2Node1.setHost("10.120.151.105");
			cluster2Node1.setPort(12201);
			cluster2Node1.setName("cluster2-node1");
			boolean cluster2Node1Result = adminService.addOrUpdateRedisNode("lindzh", "lindzh-cluster2", "cluster2-node1", cluster2Node1);
			logger.info("add redis node cluster2-node1 result:"+cluster2Node1Result);
			
			HostAndPort cluster2Node2 = new HostAndPort();
			cluster2Node2.setAlive(false);
			cluster2Node2.setHost("10.120.151.105");
			cluster2Node2.setPort(12202);
			cluster2Node2.setName("cluster2-node2");
			cluster2Node2.setMaster("cluster2-node1");
			boolean cluster2Node2Result = adminService.addOrUpdateRedisNode("lindzh", "lindzh-cluster2", "cluster2-node2", cluster2Node2);
			logger.info("add redis node cluster2-node2 result:"+cluster2Node2Result);
			//======================add backup=================
			
			ClusterStateBean backupState = new ClusterStateBean();
			backupState.setAlive(false);
			backupState.setMaster("backup-node1");
			boolean backupCluster = adminService.addOrUpdateCluster("lindzh", "lindzh-backup", backupState);
			logger.info("add cluster lindzh backupCluster result:"+backupCluster);
			
			HostAndPort backupNode1 = new HostAndPort();
			backupNode1.setAlive(false);
			backupNode1.setHost("10.120.151.105");
			backupNode1.setPort(12001);
			backupNode1.setName("backup-node1");
			boolean backupNode1Result = adminService.addOrUpdateRedisNode("lindzh", "lindzh-backup", "backup-node1", backupNode1);
			logger.info("add redis node backup-node1 result:"+backupNode1Result);
			
			HostAndPort backupNode2 = new HostAndPort();
			backupNode2.setAlive(false);
			backupNode2.setHost("10.120.151.105");
			backupNode2.setPort(12002);
			backupNode2.setMaster("backup-node1");
			backupNode2.setName("backup-node2");
			boolean backupNode2Result = adminService.addOrUpdateRedisNode("lindzh", "lindzh-backup", "backup-node2", backupNode2);
			logger.info("add redis node backup-node2 result:"+backupNode2Result);
			logger.info("create products cluster nodes success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
