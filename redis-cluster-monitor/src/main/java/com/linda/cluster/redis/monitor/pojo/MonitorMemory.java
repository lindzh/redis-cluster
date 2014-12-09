package com.linda.cluster.redis.monitor.pojo;

import lombok.Data;

@Data
public class MonitorMemory {
	private long id;
	//产品ID
	private long productId;
	//集群ID
	private long clusterId;
	//节点ID
	private long redisNodeId;
	//已使用内存数量 B
	private long used_memory;
	//分配内存(top 占用内存)
	private long used_memory_rss;
	//内存峰值
	private long used_memory_peak;
	//lua使用内存
	private long used_memory_lua;
	/**
	 *在理想情况下， used_memory_rss 的值应该只比 used_memory 稍微高一点儿。
	当 rss > used ，且两者的值相差较大时，表示存在（内部或外部的）内存碎片。
	内存碎片的比率可以通过 mem_fragmentation_ratio 的值看出。
	当 used > rss 时，表示 Redis 的部分内存被操作系统换出到交换空间了，在这种情况下，操作可能会产生明显的延迟。
	mem_fragmentation_ratio: used_memory_rss 和 used_memory 之间的比率
	 */
	private float mem_fragmentation_ratio;
	
	private String mem_allocator;
}
