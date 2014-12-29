package com.linda.cluster.redis.aof;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;


public class AofObject {
	
	@Setter
	@Getter
	private String operation;
	
	@Setter
	@Getter
	private String key;
	
	private byte[] buf;
	
	private int readIndex;
	
	private int writeIndex;
	
	private int aofEndIndex = 0;
	
	public AofObject() {
        this(32);
    }
    
    public AofObject(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        buf = new byte[size];
    }

	private void ensureCapacity(int minCapacity) {
		if (minCapacity - buf.length > 0)
			grow(minCapacity);
	}

	private void grow(int minCapacity) {
		int oldCapacity = buf.length;
		int newCapacity = oldCapacity << 1;
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity < 0) {
			if (minCapacity < 0)
				throw new OutOfMemoryError();
			newCapacity = Integer.MAX_VALUE;
		}
		buf = Arrays.copyOf(buf, newCapacity);
	}
	
    public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(writeIndex + len);
        System.arraycopy(b, off, buf, writeIndex, len);
        writeIndex += len;
    }
    
    public int write(InputStream ins,int len) throws IOException{
		ensureCapacity(writeIndex + len);
		int read = ins.read(buf, writeIndex, len);
		if(read>0){
			writeIndex += read;
		}
		return read;
    }
    
    public void write(byte b[]) {
    	this.write(b, 0, b.length);
    }
    
    public void reset() {
    	writeIndex = 0;
    }
    
    public byte toByteArray()[] {
    	return Arrays.copyOfRange(buf, readIndex, writeIndex);
    }
    
    public String toString() {
        return new String(buf, readIndex, writeIndex);
    }
	
    public synchronized int size() {
        return writeIndex-readIndex;
    }
    
    public void compact(){
    	if(readIndex>0){
        	for(int i=readIndex;i<writeIndex;i++){
        		buf[i-readIndex] = buf[i];
        	}
        	writeIndex=writeIndex-readIndex;
        	readIndex = 0;
    	}
    }
    
    public byte[] readBytes(int len){
		byte[] byteBuf = new byte[len];
		System.arraycopy(buf, readIndex, byteBuf, 0, len);
		readIndex += len;
		return byteBuf;
    }

    public byte[] readBytes(){
    	int len = writeIndex-readIndex;
		byte[] byteBuf = new byte[len];
		System.arraycopy(buf, readIndex, byteBuf, 0, len);
		readIndex+=len;
		if(readIndex>buf.length/2){
			this.compact();
		}
		return byteBuf;
    }
    
    public void clear(){
    	readIndex = 0;
    	writeIndex = 0;
    	aofEndIndex = 0;
    }
    
    private AofLen readInt(int idx){
    	int index = idx;
    	AofLen len = null;
    	int result = 0;
    	byte ch = '0';
    	while(index<=writeIndex){
    		ch = buf[index];
    		if(ch>=48&&ch<=57){
    			result = result*10+ch-48;
    		}else{
    			break;
    		}
    		index++;
    	}
    	if(index>idx&&(ch<48||ch>57)){
    		len = new AofLen();
    		len.index = index;
    		len.len = result;
    	}
    	return len;
    }
    
    public byte[] aofBytes(){
		byte[] buffer = new byte[aofEndIndex-readIndex];
		System.arraycopy(buf, readIndex, buffer, 0, buffer.length);
		readIndex = aofEndIndex;
		return buffer;
    }
    
    public boolean HasObject(){
    	if(buf[readIndex]=='*'){
    		int idx = readIndex+1;
    		AofLen len = this.readInt(idx);
    		if(len==null){
    			return false;
    		}
    		idx = len.index;
    		int c = 0;
    		idx+=2;
    		while(c<len.len&&idx<=writeIndex){
    			if(buf[idx]=='$'){
    				idx++;
    				AofLen keyLen = this.readInt(idx);
    	    		if(keyLen==null){
    	    			return false;
    	    		}
    	    		idx = keyLen.index;
    				idx+=2;
    				if(c==0){
        				byte[] buffer = new byte[keyLen.len];
        				System.arraycopy(buf, idx, buffer, 0, keyLen.len);
        				operation = new String(buffer);
    				}else if(len.len>=3&&c==1){
        				byte[] buffer = new byte[keyLen.len];
        				System.arraycopy(buf, idx, buffer, 0, keyLen.len);
        				key = new String(buffer);
    				}
    				idx+=keyLen.len+2;
    				c++;
    			}
    		}
    		if(c>=len.len){
    			aofEndIndex = idx;
    			return true;
    		}
    		return false;
    	}else{
    		return false;
    	}
    }
    
    private static class AofLen{
    	private int len = -1;
    	private int index = 0;
    }
    
    public static void main(String[] args) {
		byte[] buf = new String("*2\r\n$6\r\nSELECT\r\n$1\r\n0\r\n*3\r\n$3\r\nSET\r\n$4\r\ntest\r\n$3\r\nabc").getBytes();
		
		AofObject aof = new AofObject();
		aof.write(buf);
		
		while(aof.HasObject()){
			System.out.println(aof.key);
			System.out.println(aof.operation);
			System.out.println("-----------");
			byte[] aofBytes = aof.aofBytes();
			System.out.println(new String(aofBytes));
		}
		
    	int result = 0;
    	int index = 0;
    	byte ch = buf[index];
    	while(ch>=48&&ch<=57){
    		result = result*10+ch-48;
    		index++;
    		ch = buf[index];
    	}
    	//System.out.println(result);
	}
}
