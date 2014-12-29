package com.linda.cluster.redis.aof;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.linda.cluster.redis.common.utils.IOUtils;

public class AofObjectTest {

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
//		byte[] buf = new byte[len];
		while(fidx<flen){
//			int read = fis.read(buf,0,buf.length);
			int read = aof.write(fis, len);
			if(read>0){
				fidx+=read;
//				aof.write(buf,0,read);
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
