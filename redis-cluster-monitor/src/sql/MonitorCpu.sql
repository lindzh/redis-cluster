create table redis_monitor_cpu(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	used_cpu_sys float default 0.0,
	used_cpu_user float default 0.0,
	used_cpu_sys_children float default 0.0,
	used_cpu_user_children float default 0.0,
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

create index pdtIdx on redis_monitor_cpu(productId,addtime);
create index clusterIdx on redis_monitor_cpu(clusterId,addtime);
create index nodeIdx on redis_monitor_cpu(redisNodeId,addtime);