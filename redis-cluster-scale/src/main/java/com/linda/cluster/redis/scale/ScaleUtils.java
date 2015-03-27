package com.linda.cluster.redis.scale;

public class ScaleUtils {
	
	public static String byte2hex(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buffer.length; i++) {
			String temp = Integer.toHexString(buffer[i] & 0xFF);
			if (temp.length() == 1) {
				temp = "0" + temp;
			}
			sb.append(temp);
		}
		return sb.toString();
	}
	
}
