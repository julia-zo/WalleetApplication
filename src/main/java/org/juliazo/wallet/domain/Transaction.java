package org.juliazo.wallet.domain;

/**
 * A class representing a transaction attempted by the player, might be an accepted or rejected transaction.
 */
public class Transaction {
    /**
     * The primary identifier of the transaction.
     * Must be unique among all transactions.
     */
    private long transactionID;

    /**
     * Type of the transaction, WITHDRAWL or DEBIT
     */
    private String transactionType;

    /**
     * The amount used on a given transaction
     */
    private float transactionAmount;

    public Transaction(long transactionID, String transactionType, float transactionAmount) {
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public float getTransactionAmount() {
        return transactionAmount;
    }
}
