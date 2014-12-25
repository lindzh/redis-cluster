package com.linda.cluster.redis.keepalived.redis;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import com.linda.cluster.redis.common.bean.HostAndPort;
import com.linda.cluster.redis.keepalived.redis.RedisDataNode.RedisState;

public abstract class RedisAliveBase implements RedisAlivedListener{

	protected List<RedisAlivedListener> listeners = new ArrayList<RedisAlivedListener>();
	protected Jedis jedis;
	@Setter
	@Getter
	protected HostAndPort redisHost;
	@Getter
	protected RedisState state = RedisState.INIT;
	
	private int failCount = 0;
	
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
			failCount = 0;
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
		}
	}
	
	public void close(){
		state = RedisState.CLOSE;
		try{
			jedis.close();
			failCount = 0;
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
		}
	}
	
	public void info(){
		try{
			String info = jedis.info();
			this.onInfo(this, info);
			failCount = 0;
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
			this.connect();
		}
	}
	
	public void ping(){
		try{
			this.jedis.ping();
			this.onPing(this);
			failCount = 0;
		}catch(Exception e){
			state = RedisState.ERROR;
			this.onException(this,e);
			this.connect();
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
		failCount++;
		if(this.failCount<5){
			for(RedisAlivedListener listener:listeners){
				listener.onException(redis,e);
			}
		}
	}

	@Override
	public void onInfo(RedisAliveBase redis,String info) {
		for(RedisAlivedListener listener:listeners){
			listener.onInfo(redis, info);
		}
	}
	
	@Override
	public void onPing(RedisAliveBase redis) {
		for(RedisAlivedListener listener:listeners){
			listener.onPing(redis);
		}
	}
}
