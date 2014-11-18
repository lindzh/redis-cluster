package com.linda.cluster.redis.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linda.cluster.redis.common.bean.RedisZookeeperServiceConfig;
import com.linda.cluster.redis.common.bean.RedisZookeeperServiceConfig.RedisClientBean;
import com.linda.cluster.redis.common.bean.ShardingBean;
import com.linda.cluster.redis.common.spring.BeanInit;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

/**
 * redis ha connection get and sharding 
 * @author hzlindzh
 *
 */
public class JedisClusterSlotConnectionHandler extends JedisSlotBasedConnectionHandler implements Watcher,BeanInit{
	
	private Logger logger = Logger.getLogger(JedisClusterSlotConnectionHandler.class);
	
	private JedisPoolConfig redisPoolConfig;
	
	private RedisZookeeperServiceConfig config;
	
	private Map<String,String> hostNodeMap;
	
	private Map<String,JSONObject> dataNodeConfigMap;
	
	private Map<String,JedisPool> dataNodePoolMap;
	
	private List<ShardingBean> buckets;
	
	private ZooKeeper zooKeeper;
	
	private Object mutex = new Object();
	
	private int retrytime;
	
	private Map<String,ReadWriteLock> lock;
	
	public JedisClusterSlotConnectionHandler(JedisPoolConfig redisPoolConfig,RedisZookeeperServiceConfig config,List<ShardingBean> buckets){
		super(Collections.EMPTY_SET);
		this.redisPoolConfig = redisPoolConfig;
		this.buckets = buckets;
		this.config = config;
		initCache();
		init();
	}
	
	private void initCache(){
		lock = new ConcurrentHashMap<String,ReadWriteLock>();
		hostNodeMap = new ConcurrentHashMap<String,String>();
		dataNodeConfigMap = new ConcurrentHashMap<String,JSONObject>();
		dataNodePoolMap = new ConcurrentHashMap<String,JedisPool>();
	}
	
	private JedisClusterSlotConnectionHandler(Set<HostAndPort> nodes) {
		super(nodes);
	}

	@Override
	public Jedis getConnection() {
		throw new NotSupportRedisOperationException();
	}

	@Override
	protected JedisPool getRandomConnection() {
		throw new NotSupportRedisOperationException();
	}
	
	private String hostPortToString(String host,int port){
		return host+":"+port;
	}
	
	@Override
	public Jedis getConnectionFromSlot(int slot) {
		int bucketIndex = slot%256;//16384/256=64
		ShardingBean dataNode = buckets.get(bucketIndex);
		JSONObject config = dataNodeConfigMap.get(dataNode.getNodeName());
		if(config==null){
			logger.error("no redis node found for bucket "+dataNode.getNodeName());
			return null;
		}
		String master = RedisZookeeperUtils.getRedisMasterConfig(config);
		JedisPool pool = dataNodePoolMap.get(master);
		Jedis resource = pool.getResource();
		lock.get(dataNode.getNodeName()).readLock().lock();
		return resource;
	}
	
	@Override
	protected void returnConnection(Jedis connection) {
		String connectionPoolKey = hostPortToString(connection.getClient().getHost(),connection.getClient().getPort());
		JedisPool jedisPool = dataNodePoolMap.get(connectionPoolKey);
		lock.get(hostNodeMap.get(connectionPoolKey)).readLock().unlock();
		jedisPool.returnResource(connection);
	}

	@Override
	public void returnBrokenConnection(Jedis connection) {
		String connectionPoolKey = hostPortToString(connection.getClient().getHost(),connection.getClient().getPort());
		JedisPool jedisPool = dataNodePoolMap.get(connectionPoolKey);
		lock.get(hostNodeMap.get(connectionPoolKey)).readLock().unlock();
		jedisPool.returnBrokenResource(connection);
	}

	@Override
	public void process(WatchedEvent event) {
		if(event.getState()==Event.KeeperState.SyncConnected){//connected
			synchronized(mutex){
				mutex.notify();	
			}
		}if(event.getType()==Event.EventType.NodeDataChanged) {//master changed or slave changed
			JSONObject redisConfigData = this.getData(config.getPath());
			this.fireRedisClusterDataNodeConfig(event.getPath(),redisConfigData);
		}if(event.getType()==Event.EventType.NodeDeleted){
			logger.error("redis cluster error node deleted:"+event.getPath());
		}
	}
	
	private String genAuthInfo(String username,String password){
		return username+":"+password;
	}
	
	private void addAuthInfo(String scheme,String authInfo){
		zooKeeper.addAuthInfo(scheme, authInfo.getBytes());
	}
	
	private JSONObject getData(String path){
		while(true){
			try {
				byte[] data = zooKeeper.getData(path, this, null);
				retrytime = 0;
				if(data!=null){
					return JSONObject.parseObject(new String(data));
				}
				return null;
			} catch (ConnectionLossException e) {
				if(retrytime>config.getRetrytime()){
					logger.error("get data for "+path+" tryed "+retrytime+" didn't get ConnectionLossException");
					return null;
				}
				getData(path);
				retrytime++;
			} catch(KeeperException e){
				logger.error("zookeeper service error:"+e.getMessage());
				e.printStackTrace();
				return null;
			}catch (InterruptedException e) {
				logger.info("interrupted start to shut down");
				return null;
			}
		}
	}
	
	private void addRedisPool(String conf,String password){
		RedisClientBean confBean = RedisZookeeperUtils.getRedisClientBean(conf, password);
		if(confBean!=null&&!dataNodePoolMap.containsKey(conf)){
			JedisPool instance = RedisZookeeperUtils.getJedisPoolInstance(redisPoolConfig, confBean, config);
			dataNodePoolMap.put(conf, instance);
		}
	}
	
	private String getDataNodeName(String path){
		int index = config.getPath().length();
		return path.substring(index+1);
	}
	
	private void fireRedisClusterDataNodeConfig(String path,JSONObject dataNodeConfigData){
		String nodeName = getDataNodeName(path);
		ReadWriteLock rwLock = lock.get(nodeName);
		if(rwLock==null){
			rwLock = new ReentrantReadWriteLock(false);
			lock.put(nodeName, rwLock);
		}
		rwLock.writeLock().lock();
		dataNodeConfigMap.put(nodeName, dataNodeConfigData);
		String master = RedisZookeeperUtils.getRedisMasterConfig(dataNodeConfigData);
		hostNodeMap.put(master, nodeName);
		List<String> slaves = RedisZookeeperUtils.getRedisSlaveConfig(dataNodeConfigData);
		String password = RedisZookeeperUtils.getRedisConfigPassword(dataNodeConfigData);
		this.addRedisPool(master, password);
		for(String slave:slaves){
			hostNodeMap.put(slave, nodeName);
			this.addRedisPool(slave, password);
		}
		rwLock.writeLock().unlock();
		logger.info("load redis node "+nodeName +" path "+path+" success data:"+dataNodeConfigData.toJSONString());
	}
	
	private void getChildren(String path){
		while(true){
			try {
				List<String> children = zooKeeper.getChildren(path, this);
				logger.info("get redis children from path "+path+" children:"+JSONArray.toJSONString(children));
				getConfigForEveryNode(path,children);
				break;
			} catch(ConnectionLossException e){
				if(retrytime>config.getRetrytime()){
					logger.error("zookeeper get redis nodes children try time out has tryed "+retrytime);
					break;
				}
				getChildren(path);
				retrytime++;
			}catch (KeeperException e) {
				logger.error("zookeeper get redis nodes children try time out has tryed "+retrytime);
			} catch (InterruptedException e) {
				logger.info("interrupted exist");
			}
		}
	}
	
	private String getPath(String parent,String child){
		return parent+"/"+child;
	}
	
	private void getConfigForEveryNode(final String parent,final List<String> children){
		for(String child:children){
			String path = this.getPath(parent, child);
			JSONObject data = this.getData(path);
			if(data==null){
				logger.error("get redis cluster conf data failed for node:"+path);
				break;
			}
			this.fireRedisClusterDataNodeConfig(path, data);
		}
	}

	@Override
	public void init() {
		try {
			String connectionString = null;
			if(config.getChroot()==null||config.getChroot().isEmpty()){
				connectionString = config.getServers();
			}else{
				connectionString = config.getServers()+config.getChroot();
			}
			synchronized(mutex){
				zooKeeper = new ZooKeeper(connectionString,config.getZooTimeout(), this);
				mutex.wait();
			}
			String info = this.genAuthInfo(config.getUsername(), config.getPassword());
			this.addAuthInfo("digest", info);
			this.getChildren(config.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		try {
			zooKeeper.close();
			dataNodeConfigMap.clear();
			Set<Entry<String,JedisPool>> entrySet = dataNodePoolMap.entrySet();
			for(Entry<String,JedisPool> entry:entrySet){
				entry.getValue().destroy();
			}
			dataNodePoolMap.clear();
			lock.clear();
			buckets.clear();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
