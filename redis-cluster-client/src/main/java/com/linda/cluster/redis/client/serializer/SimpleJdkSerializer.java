package com.linda.cluster.redis.client.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SimpleJdkSerializer implements RedisSerializer{

	@Override
	public byte[] serializeKey(String key) {
		return key.getBytes();
	}

	@Override
	public byte[] serializeValue(Object value) {
		if(value instanceof Serializable){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream stream = new ObjectOutputStream(bos);
				stream.writeObject(value);
				byte[] array = bos.toByteArray();
				bos.close();
				return array;
			} catch (IOException e) {
				//can't happen
				throw new IllegalArgumentException("write object error");
			}
		}else{
			throw new IllegalArgumentException(value.getClass()+" must implements Serializable");
		}
	}

}
