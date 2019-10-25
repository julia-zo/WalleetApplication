package org.juliazo.wallet.domain;

import java.util.Collection;

public interface TransactionHistoryRepository {
    /**
     * Persists a player's transaction into their transaction history.
     *
     * @param email the player's identifier
     * @param transaction the transaction that was made
     */
    void persistTransaction(String email, Transaction transaction);

    /**
     * Lookup all of the transactions based on the player's email.
     * Creates an empty history for new players.
     *
     * @param email the player's identifier
     * @return the player's transaction history
     */
    Collection<Transaction> lookupTransactionHistory(String email);
}
