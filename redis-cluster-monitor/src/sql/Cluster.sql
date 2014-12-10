create table redis_cluster(
	id bigint primary key auto_increment,
	productId bigint default 0 comment '产品ID',
	clusterName varchar(100) not null,
	index pdtIdx(productId)
);