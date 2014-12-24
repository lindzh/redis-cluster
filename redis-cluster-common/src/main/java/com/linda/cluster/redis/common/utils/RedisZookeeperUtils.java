package com.linda.cluster.redis.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.JedisClusterCRC16;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.bean.RedisZookeeperServiceConfig;
import com.linda.cluster.redis.common.bean.RedisZookeeperServiceConfig.RedisClientBean;

public class RedisZookeeperUtils {

	public static int getSlot(String key,int num){
		int s = key.indexOf("{");
		if (s > -1) {
		    int e = key.indexOf("}", s+1);
		    if (e > -1 && e != s+1) {
			key = key.substring(s+1, e);
		    }
		}
		return getCRC16(key) % num;
	}
	
	public static int getCRC16(String key){
		int crc = 0x0000;
		for (byte b : key.getBytes()) {
		    for (int i = 0; i < 8; i++) {
			boolean bit = ((b >> (7 - i) & 1) == 1);
			boolean c15 = ((crc >> 15 & 1) == 1);
			crc <<= 1;
			if (c15 ^ bit)
			    crc ^= JedisClusterCRC16.polynomial;
		    }
		}
		return crc &= 0xffff;
	}
	
	public static String getRedisMasterConfig(JSONObject conf){
		return conf.getString("master");
	}
	
	public static List<String> getRedisSlaveConfig(JSONObject conf){
		List<String> list = new ArrayList<String>();
		JSONArray array = conf.getJSONArray("slaves");
		for(Object a:array){
			String slaveConfig = (String)a;
			list.add(slaveConfig);
		}
		return list;
	}
	
	public static byte[] getBytes(Object obj){
		String json = JSONUtils.toJson(obj);
		return json.getBytes();
	}
	
	public static String genPath(String... pathes){
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for(String path:pathes){
			sb.append(path);
			if(index<pathes.length-1){
				sb.append("/");
			}
			index++;
		}
		return sb.toString();
	}
	
	public static String getRedisConfigPassword(JSONObject conf){
		return conf.getString("password");
	}
	
	public static Stat zkSetNodeData(ZooKeeper zk,String path,int version,byte[] data,CountBean count,RedisSetNodeDataCallback callback){
		try {
			Stat stat = zk.setData(path, data, version);
			return stat;
		} catch (KeeperException e) {
			count.incr();
			if(e instanceof NoNodeException){
				return callback.onNodeNotExist(path, data,version, count);
			}else if(e instanceof ConnectionLossException){
				return callback.onConnectionLoss(path, data,version, count);
			}else{
				return callback.onZkException(path, data, version,count);
			}
		} catch (InterruptedException e) {
			
		}
		return null;
	}
	
	public static RedisZkData zkGetNodeData(ZooKeeper zk,Watcher watcher,String path,byte[] data,CountBean count,RedisGetNodeDataCallback callback){
		RedisZkData zkdata = new RedisZkData();
		try {
			byte[] dd = zk.getData(path, watcher,  zkdata.getStat());
			zkdata.setData(dd);
			return zkdata;
		} catch (KeeperException e) {
			count.incr();
			if(e instanceof NoNodeException){
				return callback.onNodeNotExist(path, data,watcher, count);
			}else if(e instanceof ConnectionLossException){
				return callback.onConnectionLoss(path,data, watcher, count);
			}else{
				return callback.onZkException(path,data, watcher, count);
			}
		} catch (InterruptedException e) {
			
		}
		return null;
	}
	
	public static boolean zkCreateNode(ZooKeeper zk,String path,byte[] data,CreateMode mode,CountBean count,ZkNodeCreateCallback callback){
		try {
			zk.create(path,data,Ids.OPEN_ACL_UNSAFE, mode);
			return true;
		} catch (KeeperException e) {
			count.incr();
			if(e instanceof NodeExistsException){
				return callback.onNodeExist(path, data,count);
			}else if(e instanceof ConnectionLossException){
				return callback.onConnectionLoss(path, data,count);
			}else{
				return callback.onZkException(path, data,count); 
			}
		} catch (InterruptedException e) {
			
		}
		return false;
	}
	
	public static List<String> zkGetChildren(ZooKeeper zk,String path,Watcher watcher,CountBean count,RedisGetChildrenCallback callback){
		try {
			return zk.getChildren(path, watcher);
		} catch (KeeperException e) {
			count.incr();
			if(e instanceof ConnectionLossException){
				return callback.onConnectionLoss(path, watcher, count);
			}else{
				return callback.onZkException(path, watcher, count);
			}
		} catch (InterruptedException e) {
			
		}
		return Collections.emptyList();
	}
	
	public static boolean zkDeleteNode(ZooKeeper zk,int version,String path,CountBean count,RedisNodeDeleteCallback callback){
		try {
			zk.delete(path, version);
			return true;
		} catch (KeeperException e) {
			count.incr();
			if(e instanceof ConnectionLossException){
				return callback.onConnectionLoss(path, version, count);
			}else if(e instanceof NoNodeException){
				return callback.onNodeNotExist(path, version, count);
			}else{
				return callback.onZkException(path, version, count);
			}
		} catch (InterruptedException e) {
			
		}
		return false;
	}
	
	//base->product->cluster->redisnode->votes->vote
	public static String getClusterName(String path){
		String[] strs = path.split("/");
		if(strs.length>=3){
			return strs[3];
		}
		return null;
	}
	
	public static String getClusterRedisNodeName(String path){
		String[] strs = path.split("/");
		if(strs.length>=4){
			return strs[4];
		}
		return null;
	}
	
	public static int getSequenceId(String node,String prefix){
		String id = node.substring(prefix.length());
		return Integer.parseInt(id);
	}
	
	public static String getMaster(List<String> nodes,String prefix){
		String result = null;
		int id = Integer.MAX_VALUE;
		for(String node:nodes){
			int sequenceId = getSequenceId(node,prefix);
			if(sequenceId<id){
				result = node;
				id = sequenceId;
			}
		}
		return result;
	}
	
	public static RedisClientBean getRedisClientBean(String conf,String password){
		int index = conf.indexOf(':');
		if(index>0){
			String[] split = conf.split(":");
			if(split.length==2){
				RedisClientBean bean = new RedisClientBean();
				bean.setHost(split[0]);
				bean.setPort(Integer.parseInt(split[1]));
				bean.setPassword(password);
				return bean;
			}
		}
		return null;
	}
	
	public static JedisPool getJedisPoolInstance(JedisPoolConfig redisPoolConfig,RedisClientBean bean,RedisZookeeperServiceConfig config){
		if(bean.getPassword()==null||bean.getPassword().isEmpty()){
			return new JedisPool(redisPoolConfig,bean.getHost(),bean.getPort(),config.getRedisTimeout());
		}else{
			return new JedisPool(redisPoolConfig,bean.getHost(),bean.getPort(),config.getRedisTimeout(),bean.getPassword());
		}
	}
}
