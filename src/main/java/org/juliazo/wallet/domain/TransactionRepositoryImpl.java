package org.juliazo.wallet.domain;

import com.google.common.collect.Maps;

import java.util.Map;

public class TransactionRepositoryImpl implements TransactionRepository {

    private Map<Long, Transaction> transactionStorage = Maps.newConcurrentMap();

    @Override
    public synchronized Transaction addTransaction(Transaction transaction) {
        return transactionStorage.putIfAbsent(transaction.getTransactionID(), transaction);
    }

    @Override
    public Transaction getTransaction(long transactionID) {
        return transactionStorage.get(transactionID);
    }
}
