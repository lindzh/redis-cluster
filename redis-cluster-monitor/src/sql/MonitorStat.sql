create table redis_monitor_stat(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	total_connections_received int default 0,
	total_commands_processed bigint default 0,
	instantaneous_ops_per_sec int default 0,
	rejected_connections int default 0,
	sync_full int default 0,
	sync_partial_ok int default 0,
	sync_partial_err int default 0,
	expired_keys bigint default 0,
	evicted_keys bigint default 0,
	keyspace_hits bigint default 0,
	keyspace_misses bigint default 0,
	pubsub_channels int default 0,
	pubsub_patterns int default 0,
	latest_fork_usec int default 0,
	
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_stat(productId,addtime);
create index clusterIdx on redis_monitor_stat(clusterId,addtime);
create index nodeIdx on redis_monitor_stat(redisNodeId,addtime);
