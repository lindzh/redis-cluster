package com.linda.cluster.redis.keepalived.redis;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

import com.linda.cluster.redis.common.Service;


public class RedisKeepAlived extends RedisAliveBase implements Runnable,Service{
	@Setter
	@Getter
	private int pingInterval;
	
	private Thread pingThread;
	
	private AtomicBoolean stop = new AtomicBoolean(false);
	
	@Override
	public void run() {
		while(!stop.get()){
			this.ping();
			super.onPing(this);
			try {
				Thread.currentThread().sleep(pingInterval);
			} catch (InterruptedException e) {
				stop.set(true);
				break;
			}
		}
	}

	@Override
	public void startup() {
		this.connect();
		pingThread = new Thread(this);
		pingThread.start();
	}

	@Override
	public void shutdown() {
		stop.set(true);
		pingThread.interrupt();
		this.close();
	}

}
