package com.linda.cluster.redis.client.cluster;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import com.linda.cluster.redis.client.JedisClient;
import com.linda.cluster.redis.client.RedisCallback;
import com.linda.cluster.redis.client.RedisResult;
import com.linda.cluster.redis.client.exception.ClusterExceptionHandler;

public abstract class JedisTemplate implements JedisClient{
	
	protected abstract Jedis getResource(final String key);
	
	protected abstract void returnResource(Jedis jedis);
	
	protected abstract void returnBrokenResource(Jedis jedis);
	
	protected abstract Jedis getResource(final byte[] key);
	
	private ClusterExceptionHandler exceptionHandler;
	
	public ClusterExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ClusterExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	private Object doExecute(Jedis jedis,RedisCallback callback){
		if(jedis==null){
			return null;
		}
		RedisResult result = new RedisResult();
		try{
			callback.callback(jedis, result);
			this.returnResource(jedis);
		}catch(Exception e){
			if(this.exceptionHandler!=null){
				exceptionHandler.handleException(jedis, e);
			}
			this.returnBrokenResource(jedis);
		}
		return result.getValue();
	}
	
	private Object executeForResult(final String key,RedisCallback callback){
		Jedis jedis = this.getResource(key);
		return this.doExecute(jedis, callback);
	}
	
	private Object executeForResult(final byte[] key,RedisCallback callback){
		Jedis jedis = this.getResource(key);
		return this.doExecute(jedis, callback);
	}
	
	public String set(final String key, final String value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.set(key, value);
				collector.setValue(result);
			}
		});
	}

	public String get(final String key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.get(key);
				collector.setValue(result);
			}
		});
	}

	public Boolean exists(final String key) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.exists(key);
				collector.setValue(result);
			}
		});
	}

	public Long persist(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.persist(key);
				collector.setValue(result);
			}
		});
	}

	public String type(final String key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.type(key);
				collector.setValue(result);
			}
		});
	}

	public Long expire(final String key, final int seconds) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.expire(key, seconds);
				collector.setValue(result);
			}
		});
	}

	public Long expireAt(final String key, final long unixTime) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.expireAt(key, unixTime);
				collector.setValue(result);
			}
		});
	}

	public Long ttl(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.ttl(key);
				collector.setValue(result);
			}
		});
	}

	public Boolean setbit(final String key, final long offset, final boolean value) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.setbit(key, offset, value);
				collector.setValue(result);
			}
		});
	}

	public Boolean setbit(final String key, final long offset,final String value) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.setbit(key, offset, value);
				collector.setValue(result);
			}
		});
	}

	public Boolean getbit(final String key, final long offset) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.getbit(key, offset);
				collector.setValue(result);
			}
		});
	}

	public Long setrange(final String key,final long offset,final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.setrange(key, offset, value);
				collector.setValue(result);
			}
		});
	}

	public String getrange(final String key,final long startOffset,final long endOffset) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.getrange(key, startOffset, endOffset);
				collector.setValue(result);
			}
		});
	}

	public String getSet(final String key,final String value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.getSet(key, value);
				collector.setValue(result);
			}
		});
	}

	public Long setnx(final String key, final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.setnx(key, value);
				collector.setValue(result);
			}
		});
	}

	public String setex(final String key,final int seconds,final String value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.setex(key, seconds, value);
				collector.setValue(result);
			}
		});
	}

	public Long decrBy(final String key,final long integer) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.decrBy(key, integer);
				collector.setValue(result);
			}
		});
	}

	public Long decr(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.decr(key);
				collector.setValue(result);
			}
		});
	}

	public Long incrBy(final String key,final long integer) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.incrBy(key, integer);
				collector.setValue(result);
			}
		});
	}

	public Long incr(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.incr(key);
				collector.setValue(result);
			}
		});
	}

	public Long append(final String key,final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.append(key, value);
				collector.setValue(result);
			}
		});
	}

	public String substr(final String key, final int start, final int end) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.substr(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long hset(final String key, final String field, final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hset(key, field, value);
				collector.setValue(result);
			}
		});
	}

	public String hget(final String key, final String field) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.hget(key, field);
				collector.setValue(result);
			}
		});
	}

	public Long hsetnx(final String key, final String field, final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hsetnx(key, field, value);
				collector.setValue(result);
			}
		});
	}

	public String hmset(final String key, final Map<String, String> hash) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.hmset(key, hash);
				collector.setValue(result);
			}
		});
	}

	public List<String> hmget(final String key, final String... fields) {
		return (List<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.hmget(key, fields);
				collector.setValue(result);
			}
		});
	}

	public Long hincrBy(final String key, final String field, final long value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hincrBy(key, field, value);
				collector.setValue(result);
			}
		});
	}

	public Boolean hexists(final String key, final String field) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.hexists(key, field);
				collector.setValue(result);
			}
		});
	}

	public Long hdel(final String key, final String... field) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hdel(key, field);
				collector.setValue(result);
			}
		});
	}

	public Long hlen(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hlen(key);
				collector.setValue(result);
			}
		});
	}

	public Set<String> hkeys(final String key) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.hkeys(key);
				collector.setValue(result);
			}
		});
	}

	public List<String> hvals(final String key) {
		return (List<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.hvals(key);
				collector.setValue(result);
			}
		});
	}

	public Map<String, String> hgetAll(final String key) {
		return (Map<String, String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Map<String, String> result = jedis.hgetAll(key);
				collector.setValue(result);
			}
		});
	}

	public Long rpush(final String key, final String... string) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.rpush(key, string);
				collector.setValue(result);
			}
		});
	}

	public Long lpush(final String key, final String... string) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.lpush(key, string);
				collector.setValue(result);
			}
		});
	}

	public Long llen(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.llen(key);
				collector.setValue(result);
			}
		});
	}

	public List<String> lrange(final String key,final long start,final long end) {
		return (List<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.lrange(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public String ltrim(final String key, final long start, final long end) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.ltrim(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public String lindex(final String key, final long index) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.lindex(key, index);
				collector.setValue(result);
			}
		});
	}

	public String lset(final String key, final long index, final String value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.lset(key, index, value);
				collector.setValue(result);
			}
		});
	}

	public Long lrem(final String key, final long count, final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.lrem(key, count, value);
				collector.setValue(result);
			}
		});
	}

	public String lpop(final String key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.lpop(key);
				collector.setValue(result);
			}
		});
	}

	public String rpop(final String key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.rpop(key);
				collector.setValue(result);
			}
		});
	}

	public Long sadd(final String key, final String... member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.sadd(key, member);
				collector.setValue(result);
			}
		});
	}

	public Set<String> smembers(final String key) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.smembers(key);
				collector.setValue(result);
			}
		});
	}

	public Long srem(final String key, final String... member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.srem(key, member);
				collector.setValue(result);
			}
		});
	}

	public String spop(final String key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.spop(key);
				collector.setValue(result);
			}
		});
	}

	public Long scard(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.scard(key);
				collector.setValue(result);
			}
		});
	}

	public Boolean sismember(final String key, final String member) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.sismember(key, member);
				collector.setValue(result);
			}
		});
	}

	public String srandmember(final String key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.srandmember(key);
				collector.setValue(result);
			}
		});
	}

	public Long strlen(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.strlen(key);
				collector.setValue(result);
			}
		});
	}

	public Long zadd(final String key, final double score, final String member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zadd(key, score, member);
				collector.setValue(result);
			}
		});
	}

	public Long zadd(final String key,final Map<String, Double> scoreMembers) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zadd(key, scoreMembers);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrange(final String key, final long start,final long end) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrange(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zrem(final String key, final String... member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zrem(key, member);
				collector.setValue(result);
			}
		});
	}

	public Double zincrby(final String key, final double score, final String member) {
		return (Double)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Double result = jedis.zincrby(key, score, member);
				collector.setValue(result);
			}
		});
	}

	public Long zrank(final String key, final String member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zrank(key, member);
				collector.setValue(result);
			}
		});
	}

	public Long zrevrank(final String key, final String member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zrevrank(key, member);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrevrange(final String key, final long start,final long end) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrevrange(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeWithScores(final String key, final long start,final long end) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeWithScores(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeWithScores(final String key, final long start,final long end) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeWithScores(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zcard(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zcard(key);
				collector.setValue(result);
			}
		});
	}

	public Double zscore(final String key, final String member) {
		return (Double)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Double result = jedis.zscore(key, member);
				collector.setValue(result);
			}
		});
	}

	public List<String> sort(final String key) {
		return (List<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.sort(key);
				collector.setValue(result);
			}
		});
	}

	public List<String> sort(final String key, final SortingParams sortingParameters) {
		return (List<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.sort(key, sortingParameters);
				collector.setValue(result);
			}
		});
	}

	public Long zcount(final String key, final double min, final double max) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zcount(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Long zcount(final String key,final String min,final String max) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zcount(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrangeByScore(final String key, final double min, final double max) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrangeByScore(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrangeByScore(final String key,final String min,final String max) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrangeByScore(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrevrangeByScore(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrangeByScore(final String key, final double min, final double max,
			final int offset, final int count) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrangeByScore(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrevrangeByScore(final String key,final String max,final String min) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrevrangeByScore(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrangeByScore(final String key,final String min,final String max,
			final int offset, final int count) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrangeByScore(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrevrangeByScore(final String key, final double max, final double min,
			final int offset, final int count) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrevrangeByScore(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max,
			final double min) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min,
			final double max, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<String> zrevrangeByScore(final String key,final String max,final String min,
			final int offset, final int count) {
		return (Set<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<String> result = jedis.zrevrangeByScore(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key,final String min,final String max) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key,final String max,
			final String min) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key,final String min,
			final String max, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max,
			final double min, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key,final String max,
			final String min, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Long zremrangeByRank(final String key, final long start,final long end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zremrangeByRank(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zremrangeByScore(final String key, final double start, final double end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zremrangeByScore(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zremrangeByScore(final String key, final String start,final String end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zremrangeByScore(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long linsert(final String key, final LIST_POSITION where, final String pivot,
			final String value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.linsert(key, where, pivot, value);
				collector.setValue(result);
			}
		});
	}

	public Long lpushx(final String key, final String... string) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.lpushx(key, string);
				collector.setValue(result);
			}
		});
	}

	public Long rpushx(final String key, final String... string) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.rpushx(key, string);
				collector.setValue(result);
			}
		});
	}

	public List<String> blpop(final String arg) {
		return (List<String>)this.executeForResult(arg, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.blpop(arg);
				collector.setValue(result);
			}
		});
	}

	public List<String> brpop(final String arg) {
		return (List<String>)this.executeForResult(arg, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<String> result = jedis.brpop(arg);
				collector.setValue(result);
			}
		});
	}

	public Long del(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.del(key);
				collector.setValue(result);
			}
		});
	}

	public String echo(final String string) {
		return (String)this.executeForResult(string, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.echo(string);
				collector.setValue(result);
			}
		});
	}

	public Long move(final String key,final int dbIndex) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.move(key, dbIndex);
				collector.setValue(result);
			}
		});
	}

	public Long bitcount(final String key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.bitcount(key);
				collector.setValue(result);
			}
		});
	}

	public Long bitcount(final String key, final long start,final long end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.bitcount(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public ScanResult<Entry<String, String>> hscan(final String key,final int cursor) {
		return (ScanResult<Entry<String, String>>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				ScanResult<Entry<String, String>> result = jedis.hscan(key, cursor);
				collector.setValue(result);
			}
		});
	}

	public ScanResult<String> sscan(final String key,final int cursor) {
		return (ScanResult<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				ScanResult<String> result = jedis.sscan(key, cursor);
				collector.setValue(result);
			}
		});
	}

	public ScanResult<Tuple> zscan(final String key,final int cursor) {
		return (ScanResult<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				ScanResult<Tuple> result = jedis.zscan(key, cursor);
				collector.setValue(result);
			}
		});
	}

	public ScanResult<Entry<String, String>> hscan(final String key, final String cursor) {
		return (ScanResult<Entry<String, String>>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				ScanResult<Entry<String, String>> result = jedis.hscan(key, cursor);
				collector.setValue(result);
			}
		});
	}

	public ScanResult<String> sscan(final String key, final String cursor) {
		return (ScanResult<String>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				ScanResult<String> result = jedis.sscan(key, cursor);
				collector.setValue(result);
			}
		});
	}

	public ScanResult<Tuple> zscan(final String key, final String cursor) {
		return (ScanResult<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				ScanResult<Tuple> result = jedis.zscan(key, cursor);
				collector.setValue(result);
			}
		});
	}

	public String set(final byte[] key, final byte[] value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.set(key, value);
				collector.setValue(result);
			}
		});
	}

	public byte[] get(final byte[] key) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.get(key);
				collector.setValue(result);
			}
		});
	}

	public Boolean exists(final byte[] key) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.exists(key);
				collector.setValue(result);
			}
		});
	}

	public Long persist(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.persist(key);
				collector.setValue(result);
			}
		});
	}

	public String type(final byte[] key) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.type(key);
				collector.setValue(result);
			}
		});
	}

	public Long expire(final byte[] key, final int seconds) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.expire(key, seconds);
				collector.setValue(result);
			}
		});
	}

	public Long expireAt(final byte[] key, final long unixTime) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.expireAt(key, unixTime);
				collector.setValue(result);
			}
		});
	}

	public Long ttl(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.ttl(key);
				collector.setValue(result);
			}
		});
	}

	public Boolean setbit(final byte[] key,final long offset, final boolean value) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.setbit(key, offset, value);
				collector.setValue(result);
			}
		});
	}

	public Boolean setbit(final byte[] key,final long offset, final byte[] value) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.setbit(key, offset, value);
				collector.setValue(result);
			}
		});
	}

	public Boolean getbit(final byte[] key,final long offset) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.getbit(key, offset);
				collector.setValue(result);
			}
		});
	}

	public Long setrange(final byte[] key,final long offset, final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.setrange(key, offset, value);
				collector.setValue(result);
			}
		});
	}

	public byte[] getrange(final byte[] key, final long startOffset,final long endOffset) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.getrange(key, startOffset, endOffset);
				collector.setValue(result);
			}
		});
	}

	public byte[] getSet(final byte[] key, final byte[] value) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.getSet(key, value);
				collector.setValue(result);
			}
		});
	}

	public Long setnx(final byte[] key, final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.setnx(key, value);
				collector.setValue(result);
			}
		});
	}

	public String setex(final byte[] key,final int seconds, final byte[] value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.setex(key, seconds, value);
				collector.setValue(result);
			}
		});
	}

	public Long decrBy(final byte[] key, final long integer) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.decrBy(key, integer);
				collector.setValue(result);
			}
		});
	}

	public Long decr(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.decr(key);
				collector.setValue(result);
			}
		});
	}

	public Long incrBy(final byte[] key, final long integer) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.incrBy(key, integer);
				collector.setValue(result);
			}
		});
	}

	public Long incr(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.incr(key);
				collector.setValue(result);
			}
		});
	}

	public Long append(final byte[] key, final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.append(key, value);
				collector.setValue(result);
			}
		});
	}

	public byte[] substr(final byte[] key,final int start,final int end) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.substr(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long hset(final byte[] key,final byte[] field, final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hset(key, field, value);
				collector.setValue(result);
			}
		});
	}

	public byte[] hget(final byte[] key,final byte[] field) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.hget(key, field);
				collector.setValue(result);
			}
		});
	}

	public Long hsetnx(final byte[] key,final byte[] field, final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hsetnx(key, field, value);
				collector.setValue(result);
			}
		});
	}

	public String hmset(final byte[] key,final  Map<byte[], byte[]> hash) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.hmset(key, hash);
				collector.setValue(result);
			}
		});
	}

	public List<byte[]> hmget(final byte[] key,final byte[]... fields) {
		return (List<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<byte[]> result = jedis.hmget(key, fields);
				collector.setValue(result);
			}
		});
	}

	public Long hincrBy(final byte[] key, final byte[] field,final long value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hincrBy(key, field, value);
				collector.setValue(result);
			}
		});
	}

	public Boolean hexists(final byte[] key,final byte[] field) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.hexists(key, field);
				collector.setValue(result);
			}
		});
	}

	public Long hdel(final byte[] key, final byte[]... field) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hdel(key, field);
				collector.setValue(result);
			}
		});
	}

	public Long hlen(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.hlen(key);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> hkeys(final byte[] key) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.hkeys(key);
				collector.setValue(result);
			}
		});
	}

	public Collection<byte[]> hvals(final byte[] key) {
		return (Collection<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Collection<byte[]> result = jedis.hvals(key);
				collector.setValue(result);
			}
		});
	}

	public Map<byte[], byte[]> hgetAll(final byte[] key) {
		return (Map<byte[], byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Map<byte[], byte[]> result = jedis.hgetAll(key);
				collector.setValue(result);
			}
		});
	}

	public Long rpush(final byte[] key, final byte[]... args) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.rpush(key, args);
				collector.setValue(result);
			}
		});
	}

	public Long lpush(final byte[] key, final byte[]... args) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.lpush(key, args);
				collector.setValue(result);
			}
		});
	}

	public Long llen(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.llen(key);
				collector.setValue(result);
			}
		});
	}

	public List<byte[]> lrange(final byte[] key, final long start,final long end) {
		return (List<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<byte[]> result = jedis.lrange(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public String ltrim(final byte[] key, final long start,final long end) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.ltrim(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public byte[] lindex(final byte[] key, final long index) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.lindex(key, index);
				collector.setValue(result);
			}
		});
	}

	public String lset(final byte[] key, final long index, final byte[] value) {
		return (String)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				String result = jedis.lset(key, index, value);
				collector.setValue(result);
			}
		});
	}

	public Long lrem(final byte[] key, final long count, final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.lrem(key, count, value);
				collector.setValue(result);
			}
		});
	}

	public byte[] lpop(final byte[] key) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.lpop(key);
				collector.setValue(result);
			}
		});
	}

	public byte[] rpop(final byte[] key) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.rpop(key);
				collector.setValue(result);
			}
		});
	}

	public Long sadd(final byte[] key, final byte[]... member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.sadd(key, member);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> smembers(final byte[] key) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.smembers(key);
				collector.setValue(result);
			}
		});
	}

	public Long srem(final byte[] key, final byte[]... member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.srem(key, member);
				collector.setValue(result);
			}
		});
	}

	public byte[] spop(final byte[] key) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.spop(key);
				collector.setValue(result);
			}
		});
	}

	public Long scard(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.scard(key);
				collector.setValue(result);
			}
		});
	}

	public Boolean sismember(final byte[] key,final byte[] member) {
		return (Boolean)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Boolean result = jedis.sismember(key, member);
				collector.setValue(result);
			}
		});
	}

	public byte[] srandmember(final byte[] key) {
		return (byte[])this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.srandmember(key);
				collector.setValue(result);
			}
		});
	}

	public Long strlen(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.strlen(key);
				collector.setValue(result);
			}
		});
	}

	public Long zadd(final byte[] key, final double score,final byte[] member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zadd(key, score, member);
				collector.setValue(result);
			}
		});
	}

	public Long zadd(final byte[] key, final Map<byte[], Double> scoreMembers) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zadd(key, scoreMembers);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrange(final byte[] key, final long start,final long end) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrange(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zrem(final byte[] key, final byte[]... member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zrem(key, member);
				collector.setValue(result);
			}
		});
	}

	public Double zincrby(final byte[] key, final double score,final byte[] member) {
		return (Double)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Double result = jedis.zincrby(key, score, member);
				collector.setValue(result);
			}
		});
	}

	public Long zrank(final byte[] key,final byte[] member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zrank(key, member);
				collector.setValue(result);
			}
		});
	}

	public Long zrevrank(final byte[] key,final byte[] member) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zrevrank(key, member);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrevrange(final byte[] key, final long start,final long end) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrevrange(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeWithScores(final byte[] key, final long start,final long end) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeWithScores(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeWithScores(final byte[] key, final long start,final long end) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeWithScores(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zcard(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zcard(key);
				collector.setValue(result);
			}
		});
	}

	public Double zscore(final byte[] key,final byte[] member) {
		return (Double)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Double result = jedis.zscore(key, member);
				collector.setValue(result);
			}
		});
	}

	public List<byte[]> sort(final byte[] key) {
		return (List<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<byte[]> result = jedis.sort(key);
				collector.setValue(result);
			}
		});
	}

	public List<byte[]> sort(final byte[] key, final SortingParams sortingParameters) {
		return (List<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<byte[]> result = jedis.sort(key, sortingParameters);
				collector.setValue(result);
			}
		});
	}

	public Long zcount(final byte[] key, final double min, final double max) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zcount(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Long zcount(final byte[] key, final byte[] min,final byte[] max) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zcount(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrangeByScore(final byte[] key, final double min, final double max) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrangeByScore(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrangeByScore(final byte[] key,final byte[] min, final byte[] max) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrangeByScore(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrevrangeByScore(final byte[] key, final double max, final double min) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrevrangeByScore(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrangeByScore(final byte[] key, final double min, final double max,
			final int offset, final int count) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrangeByScore(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrevrangeByScore(final byte[] key,final byte[] max,final byte[] min) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrevrangeByScore(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrangeByScore(final byte[] key,final byte[] min, final byte[] max,
			final int offset, final int count) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrangeByScore(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrevrangeByScore(final byte[] key, final double max, final double min,
			final int offset, final int count) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrevrangeByScore(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final double min, final double max) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final double max,
			final double min) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final byte[] key, final double min,
			final double max, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<byte[]> zrevrangeByScore(final byte[] key, final byte[] max,final byte[] min,
			final int offset, final int count) {
		return (Set<byte[]>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<byte[]> result = jedis.zrevrangeByScore(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final byte[] key,final byte[] min, final byte[] max) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final byte[] max,
			final byte[] min) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final byte[] key,final byte[] min,
			final byte[] max, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final double max,
			final double min, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final byte[] key, final byte[] max,
			final byte[] min, final int offset, final int count) {
		return (Set<Tuple>)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Set<Tuple> result = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
				collector.setValue(result);
			}
		});
	}

	public Long zremrangeByRank(final byte[] key, final long start,final long end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zremrangeByRank(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zremrangeByScore(final byte[] key, final double start, final double end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zremrangeByScore(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long zremrangeByScore(final byte[] key,final byte[] start,final byte[] end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.zremrangeByScore(key, start, end);
				collector.setValue(result);
			}
		});
	}

	public Long linsert(final byte[] key, final LIST_POSITION where,final byte[] pivot,
			final byte[] value) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.linsert(key, where, pivot, value);
				collector.setValue(result);
			}
		});
	}

	public Long lpushx(final byte[] key, final byte[]... arg) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.lpushx(key, arg);
				collector.setValue(result);
			}
		});
	}

	public Long rpushx(final byte[] key,final byte[]... arg) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.rpushx(key, arg);
				collector.setValue(result);
			}
		});
	}

	public List<byte[]> blpop(final byte[] arg) {
		return (List<byte[]>)this.executeForResult(arg, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<byte[]> result = jedis.blpop(arg);
				collector.setValue(result);
			}
		});
	}

	public List<byte[]> brpop(final byte[] arg) {
		return (List<byte[]>)this.executeForResult(arg, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				List<byte[]> result = jedis.brpop(arg);
				collector.setValue(result);
			}
		});
	}

	public Long del(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.del(key);
				collector.setValue(result);
			}
		});
	}

	public byte[] echo(final byte[] arg) {
		return (byte[])this.executeForResult(arg, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				byte[] result = jedis.echo(arg);
				collector.setValue(result);
			}
		});
	}

	public Long move(final byte[] key,final int dbIndex) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.move(key, dbIndex);
				collector.setValue(result);
			}
		});
	}

	public Long bitcount(final byte[] key) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.bitcount(key);
				collector.setValue(result);
			}
		});
	}

	public Long bitcount(final byte[] key, final long start,final long end) {
		return (Long)this.executeForResult(key, new RedisCallback(){
			public void callback(Jedis jedis, RedisResult collector) {
				Long result = jedis.bitcount(key, start, end);
				collector.setValue(result);
			}
		});
	}

}
