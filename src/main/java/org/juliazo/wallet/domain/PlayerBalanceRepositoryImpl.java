package org.juliazo.wallet.domain;

import com.google.common.collect.Maps;
import javax.ws.rs.NotFoundException;
import java.util.Map;

public class PlayerBalanceRepositoryImpl implements  PlayerBalanceRepository{

    private Map<String, Float> balanceStorage = Maps.newConcurrentMap();

    @Override
    public float getBalance(String email){
        Float balance = balanceStorage.get(email);
        if (balance == null) {
            throw new NotFoundException();
        }
        return balance;
    }

    @Override
    public synchronized void addAmountToBalance(String email, float updateAmount){
        float storedBalance = balanceStorage.getOrDefault(email, 0f);
        balanceStorage.put(email, storedBalance + updateAmount);
    }

}
