package com.linda.cluster.redis.keepalived.alived;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class AlivedWatcher implements Watcher{

	private Logger logger = Logger.getLogger(AlivedWatcher.class);
	
	private Object mutex;
	
	public AlivedWatcher(Object mutex){
		this.mutex = mutex;
	}
	
	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		EventType type = event.getType();
		KeeperState state = event.getState();
		if(state==KeeperState.SyncConnected){
			synchronized(mutex){
				mutex.notify();
			}
		}
		logger.info("keeper event:path "+path+" type:"+type.toString()+" state:"+state.toString());
	}
}
