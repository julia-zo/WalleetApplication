package org.juliazo.wallet.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionResponseV1 {

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

    /**
     * Result of the transaction.
     */
    @JsonProperty(value = "transactionResult", required = true)
    private String transactionResult;

    /**
     * The reason behind the result of the transaction.
     */
    @JsonProperty(value = "transactionReason", required = true)
    private String transactionReason;

    public TransactionResponseV1() {
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

    public String getTransactionResult() {
        return transactionResult;
    }

    public void setTransactionResult(String transactionResult) {
        this.transactionResult = transactionResult;
    }

    public String getTransactionReason() {
        return transactionReason;
    }

    public void setTransactionReason(String transactionReason) {
        this.transactionReason = transactionReason;
    }
}
