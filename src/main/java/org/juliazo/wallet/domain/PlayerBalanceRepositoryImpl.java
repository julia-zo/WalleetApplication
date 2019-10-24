package org.juliazo.wallet.domain;

import com.google.common.collect.Maps;
import org.juliazo.wallet.exception.InvalidPlayerException;

import java.util.Map;

public class PlayerBalanceRepositoryImpl implements  PlayerBalanceRepository{

    private Map<String, Float> balanceStorage = Maps.newConcurrentMap();

    @Override
    public float getBalance(String email){
        Float balance = balanceStorage.get(email);
        if (balance == null) {
            throw new InvalidPlayerException();
        }
        return balance;
    }

    @Override
    public synchronized void updateBalance(String email, float updateAmount){
        float storedBalance = balanceStorage.getOrDefault(email, 0f);
        balanceStorage.put(email, storedBalance + updateAmount);
    }

}
