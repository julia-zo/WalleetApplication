package org.juliazo.wallet.domain;

public class Transaction {
    private long transactionID;
    private String transactionType;
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
