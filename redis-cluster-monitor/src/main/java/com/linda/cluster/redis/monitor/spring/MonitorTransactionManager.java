package com.linda.cluster.redis.monitor.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class MonitorTransactionManager extends DataSourceTransactionManager implements ApplicationContextAware{

	private static final long serialVersionUID = -3177438254181907166L;

	private Logger logger = Logger.getLogger(MonitorTransactionManager.class);
	
	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		super.doBegin(transaction, definition);
		logger.info("---------start---transaction--------");
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		super.doCommit(status);
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		super.doRollback(status);
		logger.info("---------rollback---transaction--------");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
	}
}
