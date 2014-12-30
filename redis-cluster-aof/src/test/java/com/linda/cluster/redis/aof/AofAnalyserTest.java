package com.linda.cluster.redis.aof;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import redis.clients.util.Hashing;

import com.linda.cluster.redis.common.sharding.Sharding;

public class AofAnalyserTest {
	
	public static void main(String[] args) throws IOException {
		File aofFile = new File("E:\\redis-aof.aof");
		RedisAofAnalyzer aofAnalyzer = new RedisAofAnalyzer();
		aofAnalyzer.setHashing(Hashing.MD5);
		aofAnalyzer.setShardingNode("node-33");
		aofAnalyzer.setShrdingFileBasePath("E:\\aof");
		List<Sharding> shardings = new ArrayList<Sharding>();
		shardings.add(new Sharding(0,100,"node-12"));
		shardings.add(new Sharding(101,200,"node-13"));
		shardings.add(new Sharding(201,255,"node-14"));
		aofAnalyzer.setShardings(shardings);
		Map<String, String> analyse = aofAnalyzer.analyse(aofFile.getAbsolutePath());
		
	}

}
