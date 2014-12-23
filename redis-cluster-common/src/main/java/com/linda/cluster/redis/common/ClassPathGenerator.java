package com.linda.cluster.redis.common;

import java.io.File;

public class ClassPathGenerator {
	
	public static void main(String[] args) {
		String base = "D:\\work\\workspace\\redis-cluster\\redis-cluster-monitor\\src\\main\\webapp\\monitor\\lib";
		String prefix = "\"$BASEDIR\"\\";
		String seperator = ":";
		File file = new File(base);
		String files = ClassPathGenerator.genClassPath(file, prefix, seperator);
		System.out.println(files);
	}
	
	public static String genClassPath(File file,String prefix,String seperator){
		StringBuilder sb = new StringBuilder();
		if(file.isDirectory()){
			String sprefix = prefix+file.getName()+"\\";
			File[] files = file.listFiles();
			int index = 0;
			for(File f:files){
				sb.append(genClassPath(f,sprefix,seperator));
				if(index<files.length-1){
					if(sb.length()>0){
						sb.append(seperator);
					}
				}
				index++;
			}
		}else{
			String name = file.getName();
			if(name.endsWith(".jar")){
				sb.append(prefix);
				sb.append(file.getName());
			}
		}
		return sb.toString();
	}
}
