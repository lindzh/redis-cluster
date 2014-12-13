package com.linda.cluster.redis.keepalived.conf;

import java.util.List;

import lombok.Data;

/**
 * 监控集群列表
 * 在监控集群节点下创建子节点monitors:然后哥监控创建e-s节点，监控各个子节点，选举子节点宕机状态，并切换master
 * @author linda
 *
 */
@Data
public class RedisProduct {
	private String productName;
	private String zkPassword;
	private int pingInterval;//发送ping 命令时间
	private int infoInterval;//发送info命令时间
	private List<String> clusters;
	//默认选举节点宕机数量为监控节点数量的一般

}
