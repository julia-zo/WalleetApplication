package org.juliazo.wallet.domain;

public interface PlayerBalanceRepository {
    /**
     * Balance of given player
     * @param email player's identifier
     * @return the balance
     * @throws {@link javax.ws.rs.NotFoundException} when there is no balance information for the player
     */
    float getBalance(String email);

    /**
     * Adds a given amount to a player's balance.
     * If the amount is positive, the balance increases.
     * If the amount is negative, the balance decreases.
     *
     * @param email player's identifier
     * @param updateAmount value in question
     */
    void addAmountToBalance(String email, float updateAmount);

}
