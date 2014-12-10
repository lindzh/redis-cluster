create table redis_monitor_memory(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	used_memory bigint default 0,
	used_memory_rss bigint default 0,
	used_memory_peak bigint default 0,
	used_memory_lua bigint default 0,
	mem_fragmentation_ratio float default 0.0,
	mem_allocator varchar(20) default null,
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_memory(productId,addtime);
create index clusterIdx on redis_monitor_memory(clusterId,addtime);
create index nodeIdx on redis_monitor_memory(redisNodeId,addtime);