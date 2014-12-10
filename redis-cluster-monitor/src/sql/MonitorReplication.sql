create table redis_monitor_replication(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	connected_slaves int default 0,
	slaveJson varchar(100) default null,
	master_repl_offset bigint default 0,
	repl_backlog_active int default 0,
	repl_backlog_size bigint default 0,
	repl_backlog_first_byte_offset bigint default 0,
	repl_backlog_histlen bigint default 0,
	
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_replication(productId,addtime);
create index clusterIdx on redis_monitor_replication(clusterId,addtime);
create index nodeIdx on redis_monitor_replication(redisNodeId,addtime);