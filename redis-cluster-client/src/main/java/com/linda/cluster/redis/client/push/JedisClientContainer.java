package com.linda.cluster.redis.client.push;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import lombok.Data;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linda.cluster.redis.common.bean.RedisZookeeperServiceConfig;
import com.linda.cluster.redis.common.bean.ShardingBean;
import com.linda.cluster.redis.common.spring.BeanInit;
import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.IntrospectorUtils;

/**
 * redis cluster client container
 * @author hzlindzh
 *
 */
@Data
public class JedisClientContainer implements BeanInit{
	
	private Logger logger = Logger.getLogger(JedisClientContainer.class);
	
	public static final String DEFAULT_REDIS_CLUSTER_CONF = "classpath:redis-cluster.conf";
	
	public static final String DEFAULT_REDIS_SHARDING_CONF = "classpath:redis-sharding.conf";
	
	private String redisClusterConf = DEFAULT_REDIS_CLUSTER_CONF;
	private String shardingConf = DEFAULT_REDIS_CLUSTER_CONF;
	
	private JedisCluster jedisCluster;
	
	private JedisPoolConfig jedisPoolConfig;

	private RedisZookeeperServiceConfig redisZookeeperServiceConfig;
	
	private List<ShardingBean> redisShardingList;
	
	@Override
	public void init() {
		initRedisClusterConfig();
		initRedisClusterShardingConfig();
		if(jedisPoolConfig!=null&&redisZookeeperServiceConfig!=null&&redisShardingList!=null&&redisShardingList.size()>0){
			jedisCluster = new JedisClusterService(jedisPoolConfig,redisZookeeperServiceConfig,redisShardingList);
			logger.info("redis cluster client inited");
		}else{
			logger.error("init redis cluster config error env setting wrong");
		}
	}
	
	private void initRedisClusterConfig(){
		InputStream redisClusterConfInputStream;
		if(redisClusterConf.startsWith(IOUtils.CLASS_PATH_PREFIX)){
			redisClusterConfInputStream = IOUtils.getClassPathInputStream(redisClusterConf);
		}else{
			redisClusterConfInputStream = IOUtils.getFileInputStream(redisClusterConf);
		}
		if(redisClusterConfInputStream!=null){
			Properties properties = IOUtils.loadProperties(redisClusterConfInputStream);
			jedisPoolConfig = IntrospectorUtils.getInstance(JedisPoolConfig.class, properties);
			redisZookeeperServiceConfig = IntrospectorUtils.getInstance(RedisZookeeperServiceConfig.class, properties);
			IOUtils.closeInputStream(redisClusterConfInputStream);
			logger.info("load "+redisClusterConf+" successfully");
		}else{
			logger.info("load "+redisClusterConf+" failed");
		}
	}
	
	private void initRedisClusterShardingConfig(){
		InputStream redisClusterShardingConfInputStream;
		if(shardingConf.startsWith(IOUtils.CLASS_PATH_PREFIX)){
			redisClusterShardingConfInputStream = IOUtils.getClassPathInputStream(shardingConf);
		}else{
			redisClusterShardingConfInputStream = IOUtils.getFileInputStream(shardingConf);
		}
		if(redisClusterShardingConfInputStream!=null){
			logger.info("load "+shardingConf+" successfully");
			String shardingConfContent = IOUtils.toString(redisClusterShardingConfInputStream);
			IOUtils.closeInputStream(redisClusterShardingConfInputStream);
			redisShardingList = getBucketMap(shardingConfContent);
		}else{
			logger.info("load "+shardingConf+" failed");
		}
	}
	
	private List<ShardingBean> getBucketMap(String json){
		List<ShardingBean> list = new ArrayList<ShardingBean>();
		if(json!=null&&json.length()>0){
			JSONArray array = JSON.parseArray(json);
			for(Object obj:array){
				JSONObject o = (JSONObject)obj;
				Set<String> keys = o.keySet();
				if(keys!=null&&keys.iterator().hasNext()){
					String bucket = keys.iterator().next();
					String node = o.getString(bucket);
					list.add(new ShardingBean(bucket,node));
				}
			}
		}
		return list;
	}
	
	@Override
	public void destroy() {
		if(jedisCluster!=null){
			if(jedisCluster instanceof JedisClusterService){
				((JedisClusterService)jedisCluster).destroy();
			}
		}
	}
}
