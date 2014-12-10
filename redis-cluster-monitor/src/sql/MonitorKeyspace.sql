create table redis_monitor_keyspace(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
		
	databaseId int default 0,
	keynum int default 0,
	expires int default 0,
	avg_ttl int default 0,
	
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_keyspace(productId,addtime);
create index clusterIdx on redis_monitor_keyspace(clusterId,addtime);
create index nodeIdx on redis_monitor_keyspace(redisNodeId,addtime);