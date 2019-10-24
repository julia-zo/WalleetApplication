package org.juliazo.wallet.domain;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class TransactionHistoryRepositoryImpl  implements TransactionHistoryRepository{
    private Map<String, Collection<Transaction>> customerTransactionStorage = Maps.newConcurrentMap();

    @Override
    public synchronized void persistTransaction(String email, Transaction transaction) {
        Collection<Transaction> transactions = lookupTransactionHistory(email);
        transactions.add(transaction);
        customerTransactionStorage.put(email, transactions);
    }

    @Override
    public Collection<Transaction> lookupTransactionHistory(String email) {
        return customerTransactionStorage.getOrDefault(email, new LinkedList<>());
    }
}
