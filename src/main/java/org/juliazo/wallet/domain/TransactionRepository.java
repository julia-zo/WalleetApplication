package org.juliazo.wallet.domain;

public interface TransactionRepository {
    /**
     * Adds a given transaction to the repository of all transactions.
     * A transaction must have an unique identifier.
     *
     * @param transaction an executed transaction
     * @return {@code null} if the provided transaction id is unique, or the transaction currently associated
     * with the provided transaction id, in case it is not unique in the repository.
     */
    Transaction addTransaction(Transaction transaction);

    /**
     * Retrieves a transaction from the repository.
     *
     * @param transactionID the transaction unique identifier
     * @return the transaction if found, {@code null} otherwise
     */
    Transaction getTransaction(long transactionID);
}
