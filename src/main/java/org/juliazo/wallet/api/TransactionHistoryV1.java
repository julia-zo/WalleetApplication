package org.juliazo.wallet.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * A class representing the transaction history of a given player.
 */
public class TransactionHistoryV1 {
    /**
     * The email of the player.
     */
    @JsonProperty(value = "email", required = true)
    private String email;

    /**
     * The current transaction history of this player.
     */
    @JsonProperty(value = "history", required = true)
    private Collection history;

    public TransactionHistoryV1() {
    }

    public TransactionHistoryV1(String email, Collection history) {
        this.email = email;
        this.history = history;
    }

    @Override
    public String toString() {
        return String.format("TransactionHistoryV1 [email=%s, history=%s]", email, history.toString());
    }
}
