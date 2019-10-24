package org.juliazo.wallet.domain;

public interface TransactionRepository {
    Transaction addTransaction(Transaction transaction);

    Transaction getTransaction(long transactionID);
}
