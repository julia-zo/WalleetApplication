package org.juliazo.wallet.service;

import org.juliazo.wallet.api.BalanceResponseV1;
import org.juliazo.wallet.api.TransactionHistoryV1;
import org.juliazo.wallet.api.TransactionRequestV1;
import org.juliazo.wallet.api.TransactionResponseV1;
import org.juliazo.wallet.domain.PlayerBalanceRepository;
import org.juliazo.wallet.domain.Transaction;
import org.juliazo.wallet.domain.TransactionHistoryRepository;
import org.juliazo.wallet.domain.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceV1Test {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PlayerBalanceRepository playerBalanceRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    private WalletServiceV1 walletService;

    @Test
    public void withdrawlNonUniqueTransactionId() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(transactionRepository.getTransaction(eq(transactionRequest.getTransactionID())))
                .thenReturn(new Transaction(new Random().nextLong(), "WITHDRAWL", 5f));

        TransactionResponseV1 response = walletService.handleWithdrawlRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "FAIL", "Transaction ID is invalid.");
    }

    @Test
    public void withdrawlBalanceGreaterThan0() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(playerBalanceRepository.getBalance(eq(transactionRequest.getEmail()))).thenReturn(20f);

        TransactionResponseV1 response = walletService.handleWithdrawlRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "SUCCESS", "Player has sufficient balance.");
    }

    @Test
    public void withdrawlBalanceEqual0() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(playerBalanceRepository.getBalance(eq(transactionRequest.getEmail()))).thenReturn(5f);

        TransactionResponseV1 response = walletService.handleWithdrawlRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "SUCCESS", "Player has sufficient balance.");
    }

    @Test
    public void withdrawlBalanceLessThan0() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(playerBalanceRepository.getBalance(eq(transactionRequest.getEmail()))).thenReturn(4f);

        TransactionResponseV1 response = walletService.handleWithdrawlRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "FAIL", "Player has insufficient balance.");
    }

    @Test
    public void withdrawlNonUniqueTransactionIdAfterConcurrency() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(playerBalanceRepository.getBalance(eq(transactionRequest.getEmail()))).thenReturn(20f);
        when(transactionRepository.addTransaction(Matchers.any(Transaction.class)))
                .thenReturn(new Transaction(new Random().nextLong(), "WITHDRAWL", 5f));

        TransactionResponseV1 response = walletService.handleWithdrawlRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "FAIL", "Transaction ID is invalid.");
    }

    @Test
    public void depositNonUniqueTransactionId() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(transactionRepository.getTransaction(eq(transactionRequest.getTransactionID())))
                .thenReturn(new Transaction(new Random().nextLong(), "WITHDRAWL", 5f));

        TransactionResponseV1 response = walletService.handleDepositRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "FAIL", "Transaction ID is invalid.");
    }

    @Test
    public void depositBalanceOK() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        TransactionResponseV1 response = walletService.handleDepositRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "SUCCESS", "New funds credited successfully.");
    }

    @Test
    public void depositNonUniqueTransactionIdAfterConcurrency() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);

        when(transactionRepository.addTransaction(Matchers.any(Transaction.class)))
                .thenReturn(new Transaction(new Random().nextLong(), "WITHDRAWL", 5f));

        TransactionResponseV1 response = walletService.handleDepositRequestV1(transactionRequest);
        assertTransactionResult(transactionRequest, response, "FAIL", "Transaction ID is invalid.");
    }

    @Test (expected = BadRequestException.class)
    public void depositNegativeValue() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);
        transactionRequest.setTransactionAmount(-1f);

        walletService.handleDepositRequestV1(transactionRequest);
    }

    @Test (expected = NotFoundException.class)
    public void getHistoryNonExistentPlayer() {
        when(transactionHistoryRepository.lookupTransactionHistory(Matchers.any(String.class)))
                .thenReturn(Collections.emptyList());
        walletService.handleTransactionHistoryRequestV1("john@doe.com");
    }

    @Test
    public void getHistoryExistentPlayer() {
        TransactionRequestV1 transactionRequest = defaultTransactionRequest(10);
        Collection<Transaction> expected = new LinkedList<>();
        expected.add(new Transaction(transactionRequest.getTransactionID(), "DEPOSIT", transactionRequest.getTransactionAmount()));
        when(transactionHistoryRepository.lookupTransactionHistory(Matchers.any(String.class)))
                .thenReturn(expected);

        TransactionHistoryV1 response = walletService.handleTransactionHistoryRequestV1("john@doe.com");
        assertEquals(transactionRequest.getEmail(), response.getEmail());
        assertEquals(expected, response.getHistory());
    }

    @Test
    public void getBalance() {
        float expected = 5f;
        when(playerBalanceRepository.getBalance(Matchers.any(String.class)))
                .thenReturn(expected);

        BalanceResponseV1 response = walletService.handleBalanceRequestV1("john@doe.com");
        assertEquals("john@doe.com", response.getEmail());
        assertEquals(expected, response.getCurrentBalance(), 0);
    }

    private void assertTransactionResult(TransactionRequestV1 transactionRequest, TransactionResponseV1 response, String expectedResult, String expectedReason) {
        assertEquals(transactionRequest.getEmail(), response.getEmail());
        assertEquals(transactionRequest.getTransactionID(), response.getTransactionID());
        assertEquals(transactionRequest.getTransactionAmount(), response.getTransactionAmount(), 0);
        assertEquals(expectedResult, response.getTransactionResult());
        assertEquals(expectedReason, response.getTransactionReason());
    }

    private TransactionRequestV1 defaultTransactionRequest(int amount) {
        return new TransactionRequestV1("john@doe.com", 5f, Math.abs(new Random().nextLong()));
    }


}