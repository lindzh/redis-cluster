create table redis_monitor_clients(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	connected_clients int default 0,
	client_longest_output_list int default 0,
	client_biggest_input_buf int default 0,
	blocked_clients int default 0,
	
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_clients(productId,addtime);
create index clusterIdx on redis_monitor_clients(clusterId,addtime);
create index nodeIdx on redis_monitor_clients(redisNodeId,addtime);