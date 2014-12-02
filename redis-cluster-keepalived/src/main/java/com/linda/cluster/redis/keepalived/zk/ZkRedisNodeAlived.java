package com.linda.cluster.redis.keepalived.zk;

import lombok.Getter;
import lombok.Setter;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.exception.RedisZkException;
import com.linda.cluster.redis.keepalived.conf.HostAndPort;
import com.linda.cluster.redis.keepalived.utils.RedisZkUtils;

public class ZkRedisNodeAlived implements Service{
	@Setter
	@Getter
	private ZooKeeper zk;
	@Setter
	@Getter
	private String basePath;
	@Setter
	@Getter //连接执行ping 或者 info的本地ip，端口，以及name,方便在页面监控
	private HostAndPort keepAlivedHostAndPort;
	
	@Override
	public void startup() {
		this.createNode(this.getRedisKeepAlivedPath(), this.getHostAndPortData(), 0,null);
		this.createNode(getRedisKeepAlivedMasterPath(), this.getHostAndPortData(), 0, new MasterCreateCallback());
	}
	
	private void createNode(final String path,final byte[] data,int tryd,final ZkPathCreateCallback callback){
		try {
			zk.create(path, data,Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException e) {
			if(e instanceof ConnectionLossException){
				if(tryd>5){
					throw new RedisZkException(e);
				}
				this.createNode(path, data, tryd+1,callback);
			}else{
				if(callback!=null){
					callback.callback(path, data);
				}
			}
		} catch (InterruptedException e) {
			throw new RedisZkException(e);
		}
	}
	
	private byte[] getHostAndPortData(){
		String data = RedisZkUtils.toString(keepAlivedHostAndPort);
		return data.getBytes();
	}
	
	private String getRedisKeepAlivedMasterPath(){
		return this.basePath+"/alive-master";
	}
	
	private String getRedisKeepAlivedPath(){
		return this.basePath+"/"+keepAlivedHostAndPort.getName();
	}
	
	private class MasterCreateCallback implements ZkPathCreateCallback{
		public void callback(String path, byte[] data) {
		//	zk.getData(path, watcher, stat);
		}
	}
	
	@Override
	public void shutdown() {

	}
}
