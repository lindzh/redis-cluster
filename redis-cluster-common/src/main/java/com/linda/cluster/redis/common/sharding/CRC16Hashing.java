package com.linda.cluster.redis.common.sharding;

import redis.clients.util.JedisClusterCRC16;

public class CRC16Hashing implements Hashing{

	@Override
	public int hash(String key) {
		return this.getCRC16(key)%256;
	}

	public int getCRC16(String key){
		int crc = 0x0000;
		for (byte b : key.getBytes()) {
		    for (int i = 0; i < 8; i++) {
			boolean bit = ((b >> (7 - i) & 1) == 1);
			boolean c15 = ((crc >> 15 & 1) == 1);
			crc <<= 1;
			if (c15 ^ bit)
			    crc ^= JedisClusterCRC16.polynomial;
		    }
		}
		return crc &= 0xffff;
	}
	
}

