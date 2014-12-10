create table redis_monitor_server(
	id bigint primary key auto_increment,
	productId bigint not null,
	clusterId bigint not null,
	redisNodeId bigint not null,
	addtime bigint not null,
	
	redis_version varchar(20) default null,
	redis_git_sha1 varchar(20) default null,
	redis_git_dirty varchar(20) default null,
	redis_build_id varchar(20) default null,
	redis_mode varchar(20) default null,
	os varchar(20) default null,
	arch_bits int default 64,
	multiplexing_api varchar(20) default null,
	gcc_version varchar(20) default null,
	process_id int default 0,
	run_id varchar(50) default null,
	tcp_port int default 0,
	uptime_in_seconds bigint default 0,
	uptime_in_days bigint default 0,
	hz varchar(20) default null,
	lru_clock varchar(50) default null,
	config_file varchar(100) default null,
		
	index pdt_cluster_node_time_idx(productId,clusterId,redisNodeId,addtime)
);

