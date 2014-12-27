package com.linda.cluster.redis.aof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.linda.cluster.redis.common.utils.IOUtils;
import com.linda.cluster.redis.common.utils.JSONUtils;

public class RedisAofAnalyzer {
	

	public static int readCount(BufferedReader reader) throws IOException{
		int result = 0;
		char ch = (char)reader.read();
		if(ch>='0'&&ch<='9'){
			
		}
		return result;
	}
	
	public static AofObject analyseAof(BufferedReader reader) throws IOException{
		Set<String> operations = new HashSet<String>();
		Set<String> keys = new HashSet<String>();
		String line = reader.readLine();
		while(line!=null){
			if(line!=null&&line.length()>0){
				char ch = line.charAt(0);
				if(ch=='*'){
					int len = Integer.parseInt(line.substring(1).trim());
					int c = 0;
					while(c<len){
						//len
						line = reader.readLine();
						//arg
						line = reader.readLine();
						
						if(c==0){
							operations.add(line.trim());
						}
						
						if(len>=3&&c==1){
							keys.add(line);
						}
						c++;
					}
				}
			}
			line = reader.readLine();
		}
		
		System.out.println("operations:"+JSONUtils.toJson(operations));
		
		System.out.println("keys:"+JSONUtils.toJson(keys));
		
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		File aofFile = new File("E:\\redis-aof.aof");
		InputStream fis = IOUtils.getFileInputStream(aofFile.getAbsolutePath());
		File file = new File("E:\\redis-aof.backup");
		if(file.exists()){
			file.delete();
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		int len = 1024*1024;
		AofObject aof = new AofObject(len);
		long fidx = 0;
		long flen = aofFile.length();
		byte[] buf = new byte[len];
		while(fidx<flen){
			int read = fis.read(buf,0,buf.length);
			if(read>0){
				fidx+=read;
				aof.write(buf,0,read);
				while(aof.HasObject()){
					System.out.println(aof.getKey());
					System.out.println(aof.getOperation());
					System.out.println("----------------------------");
					fos.write(aof.aofBytes());
				}
			}
		}
		fos.close();
		fis.close();
	}
}
