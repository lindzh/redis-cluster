package com.linda.cluster.redis.client.linda;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

import com.linda.cluster.redis.client.utils.RedisUtils;
import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.bean.CountBean;
import com.linda.cluster.redis.common.bean.RedisProductDataBean;
import com.linda.cluster.redis.common.bean.RedisZkData;
import com.linda.cluster.redis.common.sharding.Sharding;
import com.linda.cluster.redis.common.utils.JSONUtils;
import com.linda.cluster.redis.common.utils.RedisGetNodeDataCallback;
import com.linda.cluster.redis.common.utils.RedisZookeeperUtils;

public class ClusterRedisConnectionHandler extends JedisSlotBasedConnectionHandler implements Watcher,Service{
	
	//zk service list
	private List<HostAndPort> zkhosts;
	//zk base path
	private String redisBasePath;
	//product config
	private String product;
	//product password
	private String password;
	
	private int sessionTimeout = 10000;
	
	private ZooKeeper zooKeeper;
	
	private ConcurrentHashMap<String, Sharding> productShardingConf = new ConcurrentHashMap<String, Sharding>();
	
	private Logger logger = Logger.getLogger(ClusterRedisConnectionHandler.class);
	
	public ClusterRedisConnectionHandler(List<HostAndPort> zkhosts,String product){
		this(zkhosts,product,null,null);
	}
	
	public ClusterRedisConnectionHandler(List<HostAndPort> zkhosts,String product,String password){
		this(zkhosts,product,password,null);
	}
	
	public ClusterRedisConnectionHandler(List<HostAndPort> zkhosts,String product,String password,String basepath){
		this(null);
		this.zkhosts = zkhosts;
		this.product = product;
		this.password = password;
		this.redisBasePath = basepath;
	}
	
	private ClusterRedisConnectionHandler(Set<HostAndPort> nodes) {
		super(Collections.EMPTY_SET);
	}
	
	public void returnConnection(Jedis connection){
		super.returnConnection(connection);
	}
	
	private void init(){
		String zkServers = RedisUtils.toString(zkhosts);
		try {
			zooKeeper = new ZooKeeper(zkServers,sessionTimeout,this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		
	}

	@Override
	public void process(WatchedEvent event) {
		
	}
	
	private Watcher shardingWatcher = new Watcher(){
		@Override
		public void process(WatchedEvent event) {
			
		}
	};
	
	private RedisGetNodeDataCallback shardingDataCallback = new RedisGetNodeDataCallback(){
		public RedisZkData onNodeNotExist(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}

		public RedisZkData onConnectionLoss(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}

		public RedisZkData onZkException(String path, byte[] data,Watcher watcher, CountBean count) {
			return null;
		}
	};
	
	private void getShardingData(){
		String path = RedisZookeeperUtils.genPath(this.redisBasePath,product);
		RedisZkData zkData = RedisZookeeperUtils.zkGetNodeData(zooKeeper, shardingWatcher, path, null, new CountBean(), shardingDataCallback);
		if(zkData!=null&&zkData.getData()!=null){
			String shardingJson = new String(zkData.getData());
			RedisProductDataBean sharding = JSONUtils.fromJson(shardingJson, RedisProductDataBean.class);
			this.handleShardingChange(sharding);
		}else{
			//TODO
		}
	}
	
	private void handleShardingChange(RedisProductDataBean sharding){
		List<Sharding> shardings = sharding.getSharding();
		for(Sharding shard:shardings){
			String node = shard.getNode();
			Sharding oldSharding = productShardingConf.get(node);
			if(!RedisUtils.shardingEqual(oldSharding, shard)){
				this.changeSharding(shard);
			}
		}
	}
	
	private void changeSharding(Sharding shard){
		
		
	}

	@Override
	public void startup() {

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
