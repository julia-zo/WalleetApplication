package org.juliazo.wallet.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionRequestV1 {

    /**
     * The email of the player.
     */
    @JsonProperty(value = "email", required = true)
    private String email;

    /**
     * The total amount of this transaction.
     */
    @JsonProperty(value = "transactionAmount", required = true)
    private float transactionAmount;

    /**
     * Identifier of the transaction.
     */
    @JsonProperty(value = "transactionId", required = true)
    private long transactionID;

    public TransactionRequestV1() {
    }

    public TransactionRequestV1(String email, float transactionAmount, long transactionID) {
        this.email = email;
        this.transactionAmount = transactionAmount;
        this.transactionID = transactionID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;
    }
}
