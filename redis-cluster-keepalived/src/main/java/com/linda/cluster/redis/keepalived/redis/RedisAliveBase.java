package com.linda.cluster.redis.keepalived.redis;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.keepalived.conf.HostAndPort;
import com.linda.cluster.redis.keepalived.redis.RedisDataNode.RedisState;

public abstract class RedisAliveBase implements RedisAlivedListener{

	private List<RedisAlivedListener> listeners = new ArrayList<RedisAlivedListener>();
	private Jedis jedis;
	@Setter
	@Getter
	private HostAndPort redisHost;
	@Getter
	private RedisState state = RedisState.INIT;
	
	private void init(){
		jedis = new Jedis(redisHost.getHost(),redisHost.getPort());
	}
	
	public void connect(){
		state = RedisState.CONNECTING;
		try{
			this.init();
			jedis.connect();
			state = RedisState.CONNECTED;
			this.onConnected(this);
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
		}
	}
	
	public void close(){
		state = RedisState.CLOSE;
		try{
			jedis.close();
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
		}
	}
	
	public void info(){
		try{
			String info = jedis.info();
			this.onInfo(this, info);
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
		}
	}
	
	public void ping(){
		try{
			this.jedis.ping();
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
		}
	}
	
	public void addRedisListener(RedisAlivedListener listener){
		listeners.add(listener);
	}

	@Override
	public void onConnected(RedisAliveBase redis) {
		for(RedisAlivedListener listener:listeners){
			listener.onConnected(redis);
		}
	}

	@Override
	public void onClose(RedisAliveBase redis) {
		for(RedisAlivedListener listener:listeners){
			listener.onClose(redis);
		}
	}

	@Override
	public void onException(RedisAliveBase redis,Exception e) {
		for(RedisAlivedListener listener:listeners){
			listener.onException(redis,e);
		}
		this.connect();
	}

	@Override
	public void onInfo(RedisAliveBase redis,String info) {
		for(RedisAlivedListener listener:listeners){
			listener.onInfo(redis, info);
		}
	}
}
