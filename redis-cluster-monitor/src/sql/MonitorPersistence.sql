create table redis_monitor_persistence(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	rdb_changes_since_last_save bigint default 0,
	rdb_bgsave_in_progress int default 0,
	rdb_last_save_time bigint default 0,
	rdb_last_bgsave_status varchar(10) default 0,
	rdb_last_bgsave_time_sec bigint default 0,
	rdb_current_bgsave_time_sec bigint default 0,
	aof_enabled int default 0,
	aof_rewrite_in_progress int default 0,
	aof_rewrite_scheduled int default 0,
	aof_last_rewrite_time_sec bigint default 0,
	aof_current_rewrite_time_sec bigint default 0,
	aof_last_bgrewrite_status varchar(10) default 0,
	aof_last_write_status varchar(10) default 0,
	
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_persistence(productId,addtime);
create index clusterIdx on redis_monitor_persistence(clusterId,addtime);
create index nodeIdx on redis_monitor_persistence(redisNodeId,addtime);