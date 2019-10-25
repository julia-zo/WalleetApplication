package org.juliazo.wallet.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;

public class PlayerBalanceRepositoryTest {
    private PlayerBalanceRepository playerBalanceRepository;

    @Before
    public void before() {
        playerBalanceRepository = new PlayerBalanceRepositoryImpl();
    }

    @After
    public void after() {
        playerBalanceRepository = null;
    }

    @Test (expected = NotFoundException.class)
    public void getBalanceNonExistingPlayer() {
        playerBalanceRepository.getBalance("non-existent");
    }

    @Test
    public void increaseBalance() {
        for (int i = 0; i < 5; i++) {
            playerBalanceRepository.updateBalance("john@doe.com", 4.25f);
        }
        assertEquals(5*4.25f, playerBalanceRepository.getBalance("john@doe.com"), 0);
    }

    @Test
    public void decreaseBalance() {
        playerBalanceRepository.updateBalance("jane@doe.com", 50f);
        for (int i = 0; i < 5; i++) {
            playerBalanceRepository.updateBalance("jane@doe.com", -4.25f);
        }
        assertEquals(50 - 5*4.25f, playerBalanceRepository.getBalance("jane@doe.com"), 0);
    }
}
