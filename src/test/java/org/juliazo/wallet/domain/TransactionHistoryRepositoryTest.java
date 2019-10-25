package org.juliazo.wallet.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;

public class TransactionHistoryRepositoryTest {
    private TransactionHistoryRepository transactionHistoryRepository;

    @Before
    public void before() {
        transactionHistoryRepository = new TransactionHistoryRepositoryImpl();
    }

    @After
    public void after() {
        transactionHistoryRepository = null;
    }

    @Test
    public void newCustomerTransaction() {
        Transaction playerTransaction =
                new Transaction( new Random().nextLong(),"DEPOSIT", 456f);
        transactionHistoryRepository.persistTransaction("john@doe.com", playerTransaction);
        Collection<Transaction> transactions = transactionHistoryRepository.lookupTransactionHistory("john@doe.com");

        Assert.assertEquals(1,transactions.size());
        Assert.assertEquals(playerTransaction, transactions.iterator().next());
    }
}