package com.linda.cluster.redis.monitor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linda.cluster.redis.monitor.dao.ClusterDao;
import com.linda.cluster.redis.monitor.dao.ProductDao;
import com.linda.cluster.redis.monitor.dao.RedisNodeDao;
import com.linda.cluster.redis.monitor.pojo.Cluster;
import com.linda.cluster.redis.monitor.pojo.Product;
import com.linda.cluster.redis.monitor.pojo.RedisNode;

@Service
public class RedisClusterAdminService {
	
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ClusterDao clusterDao;
	@Autowired
	private RedisNodeDao redisNodeDao;
	
	public Product addProduct(Product product){
		int add = productDao.add(product);
		if(add>0){
			return product;
		}
		return null;
	}
	
	public Cluster addCluster(Cluster cluster){
		int add = clusterDao.add(cluster);
		if(add>0){
			return cluster;
		}
		return null;
	}
	
	public RedisNode addRedisNode(RedisNode redisNode){
		int add = redisNodeDao.add(redisNode);
		if(add>0){
			return redisNode;
		}
		return null;
	}
	
	public List<Product> getAllProducts(boolean setClusters){
		List<Product> list = productDao.getAll();
		if(list!=null&&setClusters){
			for(Product pdt:list){
				this.setClusters(pdt, true);
			}
		}
		return list;
	}
	
	public Product getProduct(long id,boolean setClusters){
		Product product = productDao.getById(id);
		if(product!=null&&setClusters){
			this.setClusters(product, true);
		}
		return product;
	}
	
	private void setClusters(Product pdt,boolean setNodes){
		List<Cluster> list = clusterDao.getByProductId(pdt.getId());
		if(list!=null&&setNodes){
			for(Cluster c:list){
				this.setRedisNodes(c);
			}
		}
		pdt.setClusters(list);
	}
	
	private void setRedisNodes(Cluster cluster){
		List<RedisNode> list = redisNodeDao.getByClusterId(cluster.getProductId(), cluster.getId());
		cluster.setNodes(list);
	}
	
	public Cluster getCluster(long clusterId,boolean setRedisNodes){
		Cluster cluster = clusterDao.getById(clusterId);
		if(cluster!=null&&setRedisNodes){
			this.setRedisNodes(cluster);
		}
		return cluster;
	}
	
	public RedisNode getNodeById(long id){
		return redisNodeDao.getById(id);
	}
	
	public boolean deleteRedisNode(long id){
		return redisNodeDao.deleteById(id)>0;
	}
	
	public boolean deleteRedisCluster(long clusterId,boolean forceDelete){
		boolean result = true;
		Cluster cluster = clusterDao.getById(clusterId);
		List<RedisNode> list = redisNodeDao.getByClusterId(cluster.getProductId(), cluster.getId());
		if(list!=null&&list.size()>0){
			if(forceDelete){
				for(RedisNode node:list){
					result &= redisNodeDao.deleteById(node.getId())>0;
				}
			}else{
				result = false;	
			}
		}else{
			result &= clusterDao.deleteById(clusterId)>0;
		}
		return result;
	}
	
	public boolean deleteProduct(long productId,boolean forceDelete){
		boolean result = true;
		List<Cluster> list = clusterDao.getByProductId(productId);
		if(list!=null&&list.size()>0&&forceDelete){
			for(Cluster cluster:list){
				result &= this.deleteRedisCluster(cluster.getId(), forceDelete);
			}
		}
		result &= productDao.deleteById(productId)>0;
		return result;
	}
}
