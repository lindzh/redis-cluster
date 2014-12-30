package com.linda.cluster.redis.aof;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import redis.clients.util.Hashing;

import com.linda.cluster.redis.common.sharding.Sharding;
import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.SlotUtils;
import com.linda.cluster.redis.common.utils.TimeUtils;

@Data
public class RedisAofAnalyzer {
	
	private List<Sharding> shardings;
	
	private Map<Integer,Sharding> shardingCache = new HashMap<Integer,Sharding>();
	
	private Hashing hashing = Hashing.MD5;
	
	private String shrdingFileBasePath;
	
	private int cacheLen = 1024*1024;//1m
	
	private String shardingNode;
	
	private void initSharding(){
		for(Sharding sharding:shardings){
			for(int i=sharding.getFrom();i<=sharding.getTo();i++){
				Sharding sh = shardingCache.get(i);
				if(sh!=null){
					throw new IllegalArgumentException("dumplicate redis sharding config");
				}
				shardingCache.put(i, sharding);
			}
		}
	}
	
	
	public Map<String,String> analyse(InputStream ins,long length){
		if(ins!=null&&length>0){
			long readed = 0;
			this.initSharding();//初始化sharding
			Map<String, String> fileMap = new HashMap<String,String>();
			HashMap<String, FileOutputStream> fosMap = new HashMap<String,FileOutputStream>();
			AofObject aofObject = new AofObject(cacheLen);
			while(readed<length){
				try {
					int read = aofObject.write(ins, cacheLen);
					if(read>0){
						readed += read;
						while(aofObject.HasObject()){
							String key = aofObject.getKey();
							if(key!=null){
								String operation  = aofObject.getOperation();
								long hash = hashing.hash(key);
								int slot = SlotUtils.slot(hash);
								Sharding sharding = shardingCache.get(slot);
								if(sharding!=null){
									OutputStream os = this.getOutputStream(sharding.getNode(), fileMap, fosMap);
									os.write(aofObject.aofBytes());
								}else{
									throw new IllegalArgumentException("redis sharding config an't find for hash:"+hash);	
								}
							}else{
								for(Sharding sharding:shardings){
									OutputStream os = this.getOutputStream(sharding.getNode(), fileMap, fosMap);
									os.write(aofObject.aofBytes());
								}
							}
						}
					}
				} catch (IOException e) {
					throw new IllegalArgumentException("read ins error");	
				}finally{
					Collection<FileOutputStream> streams = fosMap.values();
					this.closeStreams(streams);
					fosMap.clear();
				}
			}
			return fileMap;
		}
		return null;
	}
	
	private <T extends Closeable> void closeStreams(Collection<T> streams){
		for(T stream:streams){
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
	
	@SuppressWarnings({ "unused", "resource" })
	private OutputStream getOutputStream(String nodeName,Map<String, String> fileMap,Map<String, FileOutputStream> fosMap) throws IOException{
		FileOutputStream fos = null;
		String nodeFile = fileMap.get(nodeName);
		if(nodeFile==null){
			String time = TimeUtils.toSimpleMinuteTime(System.currentTimeMillis());
			String filename = shardingNode+".sharding."+nodeName+"."+time+".aof";
			File file = new File(this.shrdingFileBasePath+File.separator+filename);
			file.createNewFile();
			fileMap.put(nodeName, file.getAbsolutePath());
			fos = new FileOutputStream(file);
			fosMap.put(file.getAbsolutePath(), fos);
			return fos;
		}else{
			fos = fosMap.get(nodeFile);
			return fos;
		}
	}
	
	public Map<String,String> analyse(String filename){
		File file = new File(filename);
		if(file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				try{
					Map<String, String> result = this.analyse(fis,file.length());
					return result;
				}finally{
					IOUtils.closeInputStream(fis);
				}
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("FileNotFoundException : "+filename);
			}
		}
		return null;
	}
}
