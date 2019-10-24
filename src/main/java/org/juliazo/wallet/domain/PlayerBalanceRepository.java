package org.juliazo.wallet.domain;

public interface PlayerBalanceRepository {
    float getBalance(String email);

    void updateBalance(String email, float updateAmount);

}
