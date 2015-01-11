package com.linda.cluster.redis.client.transaction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import com.linda.cluster.redis.client.JedisClient;


public class JedisTransactionTemplate implements RedisTransaction,JedisClient{

	private JedisClient client;
	
	private ThreadLocal<List<JedisOperation>> operationCache = new ThreadLocal<List<JedisOperation>>();
	
	private ThreadLocal<Boolean> threadTransaction = new ThreadLocal<Boolean>();
	
	private void clear(){
		threadTransaction.remove();
		operationCache.remove();
	}
	
	@Override
	public void startTransaction() {
		threadTransaction.set(true);
		operationCache.set(new ArrayList<JedisOperation>());
	}

	@Override
	public void commitTransaction() {
		if(threadTransaction.get()){
			List<JedisOperation> operations = operationCache.get();
			if(operations!=null){
				for(JedisOperation operation:operations){
					try {
						operation.getMethod().invoke(client, operation.getArgs());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.clear();
	}

	@Override
	public void rollbackTransaction() {
		this.clear();
	}

	public Long append(byte[] arg0, byte[] arg1) {
		return client.append(arg0, arg1);
	}

	public Long append(String arg0, String arg1) {
		return client.append(arg0, arg1);
	}

	public Long bitcount(byte[] arg0, long arg1, long arg2) {
		return client.bitcount(arg0, arg1, arg2);
	}

	public Long bitcount(byte[] arg0) {
		return client.bitcount(arg0);
	}

	public Long bitcount(String arg0, long arg1, long arg2) {
		return client.bitcount(arg0, arg1, arg2);
	}

	public Long bitcount(String arg0) {
		return client.bitcount(arg0);
	}

	public List<byte[]> blpop(byte[] arg0) {
		return client.blpop(arg0);
	}

	public List<String> blpop(String arg0) {
		return client.blpop(arg0);
	}

	public List<byte[]> brpop(byte[] arg0) {
		return client.brpop(arg0);
	}

	public List<String> brpop(String arg0) {
		return client.brpop(arg0);
	}

	public void close() {
		client.close();
	}

	public Long decr(byte[] arg0) {
		return client.decr(arg0);
	}

	public Long decr(String arg0) {
		return client.decr(arg0);
	}

	public Long decrBy(byte[] arg0, long arg1) {
		return client.decrBy(arg0, arg1);
	}

	public Long decrBy(String arg0, long arg1) {
		return client.decrBy(arg0, arg1);
	}

	public Long del(byte[] arg0) {
		return client.del(arg0);
	}

	public Long del(String arg0) {
		return client.del(arg0);
	}

	public byte[] echo(byte[] arg0) {
		return client.echo(arg0);
	}

	public String echo(String arg0) {
		return client.echo(arg0);
	}

	public Boolean exists(byte[] arg0) {
		return client.exists(arg0);
	}

	public Boolean exists(String arg0) {
		return client.exists(arg0);
	}

	public Long expire(byte[] arg0, int arg1) {
		return client.expire(arg0, arg1);
	}

	public Long expire(String arg0, int arg1) {
		return client.expire(arg0, arg1);
	}

	public Long expireAt(byte[] arg0, long arg1) {
		return client.expireAt(arg0, arg1);
	}

	public Long expireAt(String arg0, long arg1) {
		return client.expireAt(arg0, arg1);
	}

	public byte[] get(byte[] arg0) {
		return client.get(arg0);
	}

	public String get(String arg0) {
		return client.get(arg0);
	}

	public byte[] getSet(byte[] arg0, byte[] arg1) {
		return client.getSet(arg0, arg1);
	}

	public String getSet(String arg0, String arg1) {
		return client.getSet(arg0, arg1);
	}

	public Boolean getbit(byte[] arg0, long arg1) {
		return client.getbit(arg0, arg1);
	}

	public Boolean getbit(String arg0, long arg1) {
		return client.getbit(arg0, arg1);
	}

	public byte[] getrange(byte[] arg0, long arg1, long arg2) {
		return client.getrange(arg0, arg1, arg2);
	}

	public String getrange(String arg0, long arg1, long arg2) {
		return client.getrange(arg0, arg1, arg2);
	}

	public Long hdel(byte[] arg0, byte[]... arg1) {
		return client.hdel(arg0, arg1);
	}

	public Long hdel(String arg0, String... arg1) {
		return client.hdel(arg0, arg1);
	}

	public Boolean hexists(byte[] arg0, byte[] arg1) {
		return client.hexists(arg0, arg1);
	}

	public Boolean hexists(String arg0, String arg1) {
		return client.hexists(arg0, arg1);
	}

	public byte[] hget(byte[] arg0, byte[] arg1) {
		return client.hget(arg0, arg1);
	}

	public String hget(String arg0, String arg1) {
		return client.hget(arg0, arg1);
	}

	public Map<byte[], byte[]> hgetAll(byte[] arg0) {
		return client.hgetAll(arg0);
	}

	public Map<String, String> hgetAll(String arg0) {
		return client.hgetAll(arg0);
	}

	public Long hincrBy(byte[] arg0, byte[] arg1, long arg2) {
		return client.hincrBy(arg0, arg1, arg2);
	}

	public Long hincrBy(String arg0, String arg1, long arg2) {
		return client.hincrBy(arg0, arg1, arg2);
	}

	public Set<byte[]> hkeys(byte[] arg0) {
		return client.hkeys(arg0);
	}

	public Set<String> hkeys(String arg0) {
		return client.hkeys(arg0);
	}

	public Long hlen(byte[] arg0) {
		return client.hlen(arg0);
	}

	public Long hlen(String arg0) {
		return client.hlen(arg0);
	}

	public List<byte[]> hmget(byte[] arg0, byte[]... arg1) {
		return client.hmget(arg0, arg1);
	}

	public List<String> hmget(String arg0, String... arg1) {
		return client.hmget(arg0, arg1);
	}

	public String hmset(byte[] arg0, Map<byte[], byte[]> arg1) {
		return client.hmset(arg0, arg1);
	}

	public String hmset(String arg0, Map<String, String> arg1) {
		return client.hmset(arg0, arg1);
	}

	public ScanResult<Entry<String, String>> hscan(String arg0, int arg1) {
		return client.hscan(arg0, arg1);
	}

	public ScanResult<Entry<String, String>> hscan(String arg0, String arg1) {
		return client.hscan(arg0, arg1);
	}

	public Long hset(byte[] arg0, byte[] arg1, byte[] arg2) {
		return client.hset(arg0, arg1, arg2);
	}

	public Long hset(String arg0, String arg1, String arg2) {
		return client.hset(arg0, arg1, arg2);
	}

	public Long hsetnx(byte[] arg0, byte[] arg1, byte[] arg2) {
		return client.hsetnx(arg0, arg1, arg2);
	}

	public Long hsetnx(String arg0, String arg1, String arg2) {
		return client.hsetnx(arg0, arg1, arg2);
	}

	public Collection<byte[]> hvals(byte[] arg0) {
		return client.hvals(arg0);
	}

	public List<String> hvals(String arg0) {
		return client.hvals(arg0);
	}

	public Long incr(byte[] arg0) {
		return client.incr(arg0);
	}

	public Long incr(String arg0) {
		return client.incr(arg0);
	}

	public Long incrBy(byte[] arg0, long arg1) {
		return client.incrBy(arg0, arg1);
	}

	public Long incrBy(String arg0, long arg1) {
		return client.incrBy(arg0, arg1);
	}

	public byte[] lindex(byte[] arg0, long arg1) {
		return client.lindex(arg0, arg1);
	}

	public String lindex(String arg0, long arg1) {
		return client.lindex(arg0, arg1);
	}

	public Long linsert(byte[] arg0, LIST_POSITION arg1, byte[] arg2,
			byte[] arg3) {
		return client.linsert(arg0, arg1, arg2, arg3);
	}

	public Long linsert(String arg0, LIST_POSITION arg1, String arg2,
			String arg3) {
		return client.linsert(arg0, arg1, arg2, arg3);
	}

	public Long llen(byte[] arg0) {
		return client.llen(arg0);
	}

	public Long llen(String arg0) {
		return client.llen(arg0);
	}

	public byte[] lpop(byte[] arg0) {
		return client.lpop(arg0);
	}

	public String lpop(String arg0) {
		return client.lpop(arg0);
	}

	public Long lpush(byte[] arg0, byte[]... arg1) {
		return client.lpush(arg0, arg1);
	}

	public Long lpush(String arg0, String... arg1) {
		return client.lpush(arg0, arg1);
	}

	public Long lpushx(byte[] arg0, byte[]... arg1) {
		return client.lpushx(arg0, arg1);
	}

	public Long lpushx(String arg0, String... arg1) {
		return client.lpushx(arg0, arg1);
	}

	public List<byte[]> lrange(byte[] arg0, long arg1, long arg2) {
		return client.lrange(arg0, arg1, arg2);
	}

	public List<String> lrange(String arg0, long arg1, long arg2) {
		return client.lrange(arg0, arg1, arg2);
	}

	public Long lrem(byte[] arg0, long arg1, byte[] arg2) {
		return client.lrem(arg0, arg1, arg2);
	}

	public Long lrem(String arg0, long arg1, String arg2) {
		return client.lrem(arg0, arg1, arg2);
	}

	public String lset(byte[] arg0, long arg1, byte[] arg2) {
		return client.lset(arg0, arg1, arg2);
	}

	public String lset(String arg0, long arg1, String arg2) {
		return client.lset(arg0, arg1, arg2);
	}

	public String ltrim(byte[] arg0, long arg1, long arg2) {
		return client.ltrim(arg0, arg1, arg2);
	}

	public String ltrim(String arg0, long arg1, long arg2) {
		return client.ltrim(arg0, arg1, arg2);
	}

	public Long move(byte[] arg0, int arg1) {
		return client.move(arg0, arg1);
	}

	public Long move(String arg0, int arg1) {
		return client.move(arg0, arg1);
	}

	public Long persist(byte[] arg0) {
		return client.persist(arg0);
	}

	public Long persist(String arg0) {
		return client.persist(arg0);
	}

	public byte[] rpop(byte[] arg0) {
		return client.rpop(arg0);
	}

	public String rpop(String arg0) {
		return client.rpop(arg0);
	}

	public Long rpush(byte[] arg0, byte[]... arg1) {
		return client.rpush(arg0, arg1);
	}

	public Long rpush(String arg0, String... arg1) {
		return client.rpush(arg0, arg1);
	}

	public Long rpushx(byte[] arg0, byte[]... arg1) {
		return client.rpushx(arg0, arg1);
	}

	public Long rpushx(String arg0, String... arg1) {
		return client.rpushx(arg0, arg1);
	}

	public Long sadd(byte[] arg0, byte[]... arg1) {
		return client.sadd(arg0, arg1);
	}

	public Long sadd(String arg0, String... arg1) {
		return client.sadd(arg0, arg1);
	}

	public Long scard(byte[] arg0) {
		return client.scard(arg0);
	}

	public Long scard(String arg0) {
		return client.scard(arg0);
	}

	public String set(byte[] arg0, byte[] arg1) {
		return client.set(arg0, arg1);
	}

	public String set(String arg0, String arg1) {
		return client.set(arg0, arg1);
	}

	public Boolean setbit(byte[] arg0, long arg1, boolean arg2) {
		return client.setbit(arg0, arg1, arg2);
	}

	public Boolean setbit(byte[] arg0, long arg1, byte[] arg2) {
		return client.setbit(arg0, arg1, arg2);
	}

	public Boolean setbit(String arg0, long arg1, boolean arg2) {
		return client.setbit(arg0, arg1, arg2);
	}

	public Boolean setbit(String arg0, long arg1, String arg2) {
		return client.setbit(arg0, arg1, arg2);
	}

	public String setex(byte[] arg0, int arg1, byte[] arg2) {
		return client.setex(arg0, arg1, arg2);
	}

	public String setex(String arg0, int arg1, String arg2) {
		return client.setex(arg0, arg1, arg2);
	}

	public Long setnx(byte[] arg0, byte[] arg1) {
		return client.setnx(arg0, arg1);
	}

	public Long setnx(String arg0, String arg1) {
		return client.setnx(arg0, arg1);
	}

	public Long setrange(byte[] arg0, long arg1, byte[] arg2) {
		return client.setrange(arg0, arg1, arg2);
	}

	public Long setrange(String arg0, long arg1, String arg2) {
		return client.setrange(arg0, arg1, arg2);
	}

	public Boolean sismember(byte[] arg0, byte[] arg1) {
		return client.sismember(arg0, arg1);
	}

	public Boolean sismember(String arg0, String arg1) {
		return client.sismember(arg0, arg1);
	}

	public Set<byte[]> smembers(byte[] arg0) {
		return client.smembers(arg0);
	}

	public Set<String> smembers(String arg0) {
		return client.smembers(arg0);
	}

	public List<byte[]> sort(byte[] arg0, SortingParams arg1) {
		return client.sort(arg0, arg1);
	}

	public List<byte[]> sort(byte[] arg0) {
		return client.sort(arg0);
	}

	public List<String> sort(String arg0, SortingParams arg1) {
		return client.sort(arg0, arg1);
	}

	public List<String> sort(String arg0) {
		return client.sort(arg0);
	}

	public byte[] spop(byte[] arg0) {
		return client.spop(arg0);
	}

	public String spop(String arg0) {
		return client.spop(arg0);
	}

	public byte[] srandmember(byte[] arg0) {
		return client.srandmember(arg0);
	}

	public String srandmember(String arg0) {
		return client.srandmember(arg0);
	}

	public Long srem(byte[] arg0, byte[]... arg1) {
		return client.srem(arg0, arg1);
	}

	public Long srem(String arg0, String... arg1) {
		return client.srem(arg0, arg1);
	}

	public ScanResult<String> sscan(String arg0, int arg1) {
		return client.sscan(arg0, arg1);
	}

	public ScanResult<String> sscan(String arg0, String arg1) {
		return client.sscan(arg0, arg1);
	}

	public Long strlen(byte[] arg0) {
		return client.strlen(arg0);
	}

	public Long strlen(String arg0) {
		return client.strlen(arg0);
	}

	public byte[] substr(byte[] arg0, int arg1, int arg2) {
		return client.substr(arg0, arg1, arg2);
	}

	public String substr(String arg0, int arg1, int arg2) {
		return client.substr(arg0, arg1, arg2);
	}

	public Long ttl(byte[] arg0) {
		return client.ttl(arg0);
	}

	public Long ttl(String arg0) {
		return client.ttl(arg0);
	}

	public String type(byte[] arg0) {
		return client.type(arg0);
	}

	public String type(String arg0) {
		return client.type(arg0);
	}

	public Long zadd(byte[] arg0, double arg1, byte[] arg2) {
		return client.zadd(arg0, arg1, arg2);
	}

	public Long zadd(byte[] arg0, Map<byte[], Double> arg1) {
		return client.zadd(arg0, arg1);
	}

	public Long zadd(String arg0, double arg1, String arg2) {
		return client.zadd(arg0, arg1, arg2);
	}

	public Long zadd(String arg0, Map<String, Double> arg1) {
		return client.zadd(arg0, arg1);
	}

	public Long zcard(byte[] arg0) {
		return client.zcard(arg0);
	}

	public Long zcard(String arg0) {
		return client.zcard(arg0);
	}

	public Long zcount(byte[] arg0, byte[] arg1, byte[] arg2) {
		return client.zcount(arg0, arg1, arg2);
	}

	public Long zcount(byte[] arg0, double arg1, double arg2) {
		return client.zcount(arg0, arg1, arg2);
	}

	public Long zcount(String arg0, double arg1, double arg2) {
		return client.zcount(arg0, arg1, arg2);
	}

	public Long zcount(String arg0, String arg1, String arg2) {
		return client.zcount(arg0, arg1, arg2);
	}

	public Double zincrby(byte[] arg0, double arg1, byte[] arg2) {
		return client.zincrby(arg0, arg1, arg2);
	}

	public Double zincrby(String arg0, double arg1, String arg2) {
		return client.zincrby(arg0, arg1, arg2);
	}

	public Set<byte[]> zrange(byte[] arg0, long arg1, long arg2) {
		return client.zrange(arg0, arg1, arg2);
	}

	public Set<String> zrange(String arg0, long arg1, long arg2) {
		return client.zrange(arg0, arg1, arg2);
	}

	public Set<byte[]> zrangeByScore(byte[] arg0, byte[] arg1, byte[] arg2,
			int arg3, int arg4) {
		return client.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<byte[]> zrangeByScore(byte[] arg0, byte[] arg1, byte[] arg2) {
		return client.zrangeByScore(arg0, arg1, arg2);
	}

	public Set<byte[]> zrangeByScore(byte[] arg0, double arg1, double arg2,
			int arg3, int arg4) {
		return client.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<byte[]> zrangeByScore(byte[] arg0, double arg1, double arg2) {
		return client.zrangeByScore(arg0, arg1, arg2);
	}

	public Set<String> zrangeByScore(String arg0, double arg1, double arg2,
			int arg3, int arg4) {
		return client.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<String> zrangeByScore(String arg0, double arg1, double arg2) {
		return client.zrangeByScore(arg0, arg1, arg2);
	}

	public Set<String> zrangeByScore(String arg0, String arg1, String arg2,
			int arg3, int arg4) {
		return client.zrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<String> zrangeByScore(String arg0, String arg1, String arg2) {
		return client.zrangeByScore(arg0, arg1, arg2);
	}

	public Set<Tuple> zrangeByScoreWithScores(byte[] arg0, byte[] arg1,
			byte[] arg2, int arg3, int arg4) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrangeByScoreWithScores(byte[] arg0, byte[] arg1,
			byte[] arg2) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrangeByScoreWithScores(byte[] arg0, double arg1,
			double arg2, int arg3, int arg4) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrangeByScoreWithScores(byte[] arg0, double arg1,
			double arg2) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrangeByScoreWithScores(String arg0, double arg1,
			double arg2, int arg3, int arg4) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrangeByScoreWithScores(String arg0, double arg1,
			double arg2) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrangeByScoreWithScores(String arg0, String arg1,
			String arg2, int arg3, int arg4) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrangeByScoreWithScores(String arg0, String arg1,
			String arg2) {
		return client.zrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrangeWithScores(byte[] arg0, long arg1, long arg2) {
		return client.zrangeWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrangeWithScores(String arg0, long arg1, long arg2) {
		return client.zrangeWithScores(arg0, arg1, arg2);
	}

	public Long zrank(byte[] arg0, byte[] arg1) {
		return client.zrank(arg0, arg1);
	}

	public Long zrank(String arg0, String arg1) {
		return client.zrank(arg0, arg1);
	}

	public Long zrem(byte[] arg0, byte[]... arg1) {
		return client.zrem(arg0, arg1);
	}

	public Long zrem(String arg0, String... arg1) {
		return client.zrem(arg0, arg1);
	}

	public Long zremrangeByRank(byte[] arg0, long arg1, long arg2) {
		return client.zremrangeByRank(arg0, arg1, arg2);
	}

	public Long zremrangeByRank(String arg0, long arg1, long arg2) {
		return client.zremrangeByRank(arg0, arg1, arg2);
	}

	public Long zremrangeByScore(byte[] arg0, byte[] arg1, byte[] arg2) {
		return client.zremrangeByScore(arg0, arg1, arg2);
	}

	public Long zremrangeByScore(byte[] arg0, double arg1, double arg2) {
		return client.zremrangeByScore(arg0, arg1, arg2);
	}

	public Long zremrangeByScore(String arg0, double arg1, double arg2) {
		return client.zremrangeByScore(arg0, arg1, arg2);
	}

	public Long zremrangeByScore(String arg0, String arg1, String arg2) {
		return client.zremrangeByScore(arg0, arg1, arg2);
	}

	public Set<byte[]> zrevrange(byte[] arg0, long arg1, long arg2) {
		return client.zrevrange(arg0, arg1, arg2);
	}

	public Set<String> zrevrange(String arg0, long arg1, long arg2) {
		return client.zrevrange(arg0, arg1, arg2);
	}

	public Set<byte[]> zrevrangeByScore(byte[] arg0, byte[] arg1, byte[] arg2,
			int arg3, int arg4) {
		return client.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<byte[]> zrevrangeByScore(byte[] arg0, byte[] arg1, byte[] arg2) {
		return client.zrevrangeByScore(arg0, arg1, arg2);
	}

	public Set<byte[]> zrevrangeByScore(byte[] arg0, double arg1, double arg2,
			int arg3, int arg4) {
		return client.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<byte[]> zrevrangeByScore(byte[] arg0, double arg1, double arg2) {
		return client.zrevrangeByScore(arg0, arg1, arg2);
	}

	public Set<String> zrevrangeByScore(String arg0, double arg1, double arg2,
			int arg3, int arg4) {
		return client.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<String> zrevrangeByScore(String arg0, double arg1, double arg2) {
		return client.zrevrangeByScore(arg0, arg1, arg2);
	}

	public Set<String> zrevrangeByScore(String arg0, String arg1, String arg2,
			int arg3, int arg4) {
		return client.zrevrangeByScore(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<String> zrevrangeByScore(String arg0, String arg1, String arg2) {
		return client.zrevrangeByScore(arg0, arg1, arg2);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(byte[] arg0, byte[] arg1,
			byte[] arg2, int arg3, int arg4) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(byte[] arg0, byte[] arg1,
			byte[] arg2) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(byte[] arg0, double arg1,
			double arg2, int arg3, int arg4) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(byte[] arg0, double arg1,
			double arg2) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, double arg1,
			double arg2, int arg3, int arg4) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, double arg1,
			double arg2) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, String arg1,
			String arg2, int arg3, int arg4) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2, arg3, arg4);
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, String arg1,
			String arg2) {
		return client.zrevrangeByScoreWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrevrangeWithScores(byte[] arg0, long arg1, long arg2) {
		return client.zrevrangeWithScores(arg0, arg1, arg2);
	}

	public Set<Tuple> zrevrangeWithScores(String arg0, long arg1, long arg2) {
		return client.zrevrangeWithScores(arg0, arg1, arg2);
	}

	public Long zrevrank(byte[] arg0, byte[] arg1) {
		return client.zrevrank(arg0, arg1);
	}

	public Long zrevrank(String arg0, String arg1) {
		return client.zrevrank(arg0, arg1);
	}

	public ScanResult<Tuple> zscan(String arg0, int arg1) {
		return client.zscan(arg0, arg1);
	}

	public ScanResult<Tuple> zscan(String arg0, String arg1) {
		return client.zscan(arg0, arg1);
	}

	public Double zscore(byte[] arg0, byte[] arg1) {
		return client.zscore(arg0, arg1);
	}

	public Double zscore(String arg0, String arg1) {
		return client.zscore(arg0, arg1);
	}
}
