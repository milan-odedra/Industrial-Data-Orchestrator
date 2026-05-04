package com.ahk.samples.service;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

final class StubPlatformTransactionManager extends AbstractPlatformTransactionManager {
    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new Object();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
    }

    @Override
    protected void doCommit(org.springframework.transaction.support.DefaultTransactionStatus status) throws TransactionException {
    }

    @Override
    protected void doRollback(org.springframework.transaction.support.DefaultTransactionStatus status) throws TransactionException {
    }
}
