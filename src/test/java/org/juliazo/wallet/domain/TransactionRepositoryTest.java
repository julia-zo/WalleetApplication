package org.juliazo.wallet.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TransactionRepositoryTest {
    private TransactionRepository transactionRepository;

    @Before
    public void before() {
        transactionRepository = new TransactionRepositoryImpl();
    }

    @After
    public void after() {
        transactionRepository = null;
    }

    @Test
    public void newCustomerTransaction() {
        long transactionId = new Random().nextLong();
        Transaction playerTransaction =
                new Transaction( transactionId,"DEPOSIT", 456f);
        transactionRepository.addTransaction(playerTransaction);
        Transaction actual = transactionRepository.getTransaction(transactionId);
        assertEquals(playerTransaction, actual);
    }

}