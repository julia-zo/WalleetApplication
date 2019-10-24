package org.juliazo.wallet.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceResponseV1 {
    /**
     * The email of the player.
     */
    @JsonProperty(value = "email", required = true)
    private String email;

    /**
     * The current balance for this player
     */
    @JsonProperty(value = "currentBalance", required = true)
    private float currentBalance;

    public BalanceResponseV1() {
    }

    public BalanceResponseV1(String email, float currentBalance) {
        this.email = email;
        this.currentBalance = currentBalance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(float currentBalance) {
        this.currentBalance = currentBalance;
    }
}
