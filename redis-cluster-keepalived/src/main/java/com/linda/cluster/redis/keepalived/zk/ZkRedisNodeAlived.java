package com.linda.cluster.redis.keepalived.zk;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.linda.cluster.redis.common.Service;
import com.linda.cluster.redis.common.exception.RedisZkException;
import com.linda.cluster.redis.common.utils.JSONUtils;
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
	
	private Logger logger = Logger.getLogger(ZkRedisNodeAlived.class);
	
	private final String MASTER_NODE_NAME = "alive-master";
	
	@Override
	public void startup() {
		this.createNode(this.getRedisKeepAlivedPath(), this.getHostAndPortData(), 0,null);
		this.createNode(getRedisKeepAlivedMasterPath(), this.getHostAndPortData(), 0, null);
		HostAndPort masterHost = this.getData(getRedisKeepAlivedMasterPath(), null, 0);
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
			}else if(e instanceof NodeExistsException){
				return;
			}else{
				throw new RedisZkException(e);
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
		return this.basePath+"/"+MASTER_NODE_NAME;
	}
	
	private String getRedisKeepAlivedPath(){
		return this.basePath+"/"+keepAlivedHostAndPort.getName();
	}
	
	private HostAndPort getData(String path,Watcher watcher,int tryed){
		byte[] data = null;
		if(tryed>5){
			logger.error("too many try connect time");
			return null;
		}
		try {
			data = zk.getData(path, watcher, new Stat());
		} catch (NoNodeException e) {
			this.createNode(path, getHostAndPortData(), 0, null);
			return this.getData(path, watcher, tryed+1);
		}catch(ConnectionLossException e){
			logger.error("connection to zk loss start to connect again");
			return this.getData(path, watcher, tryed+1);
		} catch(KeeperException e){
			logger.error("getData:path:"+path+"KeeperException "+e.getMessage());
		}
		catch (InterruptedException e) {
			throw new RedisZkException(e);
		}
		if(data!=null){
			String json = new String(data);
			return JSONUtils.fromJson(json, HostAndPort.class);
		}
		return null;
	}
	
	@Override
	public void shutdown() {

	}
}
