create table redis_node(
	id bigint primary key auto_increment,
	productId bigint default 0 comment '产品ID',
	clusterId bigint default 0 comment '集群ID',
	name varchar(100) not null comment '节点名称',
	host varchar(50) not null comment '主机',
	port int default 0 comment '端口',
	index pdt(productId),
	unique key hostPortIdx(host,port)
);