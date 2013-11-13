package com.mossle.core.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/**
 * 考虑到TransactionManager中也可能因为业务进行手工回滚的情况 这种情况下不应该抛出异常
 * 
 * 如果出现局部异常导致整体事务失败的情况，可以打开 org.springframework.transaction.interceptor.TransactionInterceptor的trace日志
 * 就可以看到具体是因为什么异常导致的事务回滚
 * 
 * 日志格式如下 "Completing transaction for [" + txInfo.getJoinpointIdentification() + "] after exception: " + ex);
 */
public class ReadOnlyTransactionManager implements PlatformTransactionManager {
    /** logger. */
    private static Logger logger = LoggerFactory
            .getLogger(ReadOnlyTransactionManager.class);
    private PlatformTransactionManager platformTransactionManager;
    private boolean readOnly;

    public TransactionStatus getTransaction(TransactionDefinition definition)
            throws TransactionException {
        if (readOnly) {
            TransactionDefinition readOnlyTransactionDefinition = new ReadOnlyTransactionDefinition(
                    definition);

            return platformTransactionManager
                    .getTransaction(readOnlyTransactionDefinition);
        } else {
            return platformTransactionManager.getTransaction(definition);
        }
    }

    public void commit(TransactionStatus status) throws TransactionException {
        platformTransactionManager.commit(status);
    }

    public void rollback(TransactionStatus status) throws TransactionException {
        logger.info("transaction rollback at : {}", status);
        platformTransactionManager.rollback(status);
    }

    public void setPlatformTransactionManager(
            PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
