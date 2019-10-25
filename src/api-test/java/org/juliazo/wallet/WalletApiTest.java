package org.juliazo.wallet;

import org.juliazo.wallet.api.BalanceResponseV1;
import org.juliazo.wallet.api.TransactionHistoryV1;
import org.juliazo.wallet.api.TransactionRequestV1;
import org.juliazo.wallet.api.TransactionResponseV1;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class WalletApiTest {
    private static String SERVICE_URL = "http://localhost:8080/v1/";
    private static String BALANCE_ENDPOINT = "balance";
    private static String HISTORY_ENDPOINT = "history";
    private static String WITHDRAWL_ENDPOINT = "withdrawl";
    private static String DEPOSIT_ENDPOINT = "deposit";

    @Rule
    public final JettyServerResource server = new JettyServerResource();

    @Test
    public void getBalanceForNonExistentPlayer() {
        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+BALANCE_ENDPOINT).path(new Random().nextInt() + "@email.com").request().get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void getHistoryForNonExistentPlayer() {
        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+HISTORY_ENDPOINT).path(new Random().nextInt() + "@email.com").request().get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void withdrawlFromNonExistentPlayer() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        Response response = withdrawlPostRequest(requestPayload);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void depositToNewPlayer() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);
    }

    @Test
    public void depositToExistentPlayerCheckBalance() {
        float transactionAmount = 5f;
        TransactionRequestV1 requestPayload = defaultTransactionRequest(transactionAmount);
        for (int i = 0; i < 2; i++) {
            successfulDeposit(requestPayload);
            requestPayload.setTransactionID(requestPayload.getTransactionID() + 1l);
        }

        Response balanceResponse = ClientBuilder.newClient()
                .target(SERVICE_URL+BALANCE_ENDPOINT).path(requestPayload.getEmail()).request().get();
        assertEquals(200, balanceResponse.getStatus());
        BalanceResponseV1 balanceResponsePayload = balanceResponse.readEntity(BalanceResponseV1.class);
        assertEquals(requestPayload.getEmail(), balanceResponsePayload.getEmail());
        assertEquals(transactionAmount*2, balanceResponsePayload.getCurrentBalance(),0);
    }

    @Test
    public void depositValueZero() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(0f);
        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+DEPOSIT_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
    }

    @Test
    public void withdrawlValueZero() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);
        requestPayload = defaultTransactionRequest(0f);
        Response response = withdrawlPostRequest(requestPayload);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void withdrawlInsufficientFunds() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);

        requestPayload.setTransactionAmount(5.25f);
        requestPayload.setTransactionID(requestPayload.getTransactionID() + 1l);
        Response response = withdrawlPostRequest(requestPayload);
        TransactionResponseV1 responsePayload = assertFailedPostRequest(requestPayload, response);
        assertEquals("Player has insufficient balance.", responsePayload.getTransactionReason());
    }

    @Test
    public void withdrawlSufficientFundsCheckBalance() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);

        requestPayload.setTransactionAmount(0.25f);
        requestPayload.setTransactionID(requestPayload.getTransactionID() + 1l);
        Response response = withdrawlPostRequest(requestPayload);
        TransactionResponseV1 responsePayload = assertSuccessfulPostRequest(requestPayload, response);
        assertEquals("Player has sufficient balance.", responsePayload.getTransactionReason());

        Response balanceResponse = ClientBuilder.newClient()
                .target(SERVICE_URL+BALANCE_ENDPOINT).path(requestPayload.getEmail()).request().get();
        assertEquals(200, balanceResponse.getStatus());
        BalanceResponseV1 balanceResponsePayload = balanceResponse.readEntity(BalanceResponseV1.class);
        assertEquals(requestPayload.getEmail(), balanceResponsePayload.getEmail());
        assertEquals(4.75f, balanceResponsePayload.getCurrentBalance(),0);
    }

    @Test
    public void withdrawlUsingSameTransactionID() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);

        requestPayload.setTransactionAmount(0.25f);
        requestPayload.setTransactionID(requestPayload.getTransactionID() + 1l);
        Response response = withdrawlPostRequest(requestPayload);
        TransactionResponseV1 responsePayload = assertSuccessfulPostRequest(requestPayload, response);
        assertEquals("Player has sufficient balance.", responsePayload.getTransactionReason());
        response = withdrawlPostRequest(requestPayload);
        responsePayload = assertFailedPostRequest(requestPayload, response);
        assertEquals("Transaction ID is invalid.", responsePayload.getTransactionReason());
    }

    @Test
    public void depositUsingSameTransactionID() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);
        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL+DEPOSIT_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));
        TransactionResponseV1 responsePayload = assertFailedPostRequest(requestPayload, response);
        assertEquals("Transaction ID is invalid.", responsePayload.getTransactionReason());
    }

    @Test
    public void getHistoryForExistingPlayer() {
        TransactionRequestV1 requestPayload = defaultTransactionRequest(5f);
        successfulDeposit(requestPayload);

        requestPayload.setTransactionAmount(0.25f);
        requestPayload.setTransactionID(requestPayload.getTransactionID() + 1l);
        Response response = withdrawlPostRequest(requestPayload);
        TransactionResponseV1 responsePayload = assertSuccessfulPostRequest(requestPayload, response);
        assertEquals("Player has sufficient balance.", responsePayload.getTransactionReason());

        Response historyResponse = ClientBuilder.newClient()
                .target(SERVICE_URL+HISTORY_ENDPOINT).path(requestPayload.getEmail()).request().get();
        assertEquals(200, historyResponse.getStatus());
        TransactionHistoryV1 historyResponsePayload = historyResponse.readEntity(TransactionHistoryV1.class);
        assertEquals(requestPayload.getEmail(), historyResponsePayload.getEmail());
        assertEquals(2, historyResponsePayload.getHistory().size());
    }

    private void successfulDeposit(TransactionRequestV1 requestPayload) {
        Response response = ClientBuilder.newClient()
                .target(SERVICE_URL + DEPOSIT_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));

        TransactionResponseV1 responsePayload = assertSuccessfulPostRequest(requestPayload, response);
        assertEquals("New funds credited successfully.", responsePayload.getTransactionReason());
    }

    private Response withdrawlPostRequest(TransactionRequestV1 requestPayload) {
        return ClientBuilder.newClient()
                .target(SERVICE_URL+WITHDRAWL_ENDPOINT).request()
                .post(Entity.entity(requestPayload, MediaType.APPLICATION_JSON));
    }

    private TransactionResponseV1 assertSuccessfulPostRequest(TransactionRequestV1 requestPayload, Response response) {
        assertEquals(200, response.getStatus());
        TransactionResponseV1 responsePayload = response.readEntity(TransactionResponseV1.class);
        assertEquals(requestPayload.getEmail(), responsePayload.getEmail());
        assertEquals(requestPayload.getTransactionAmount(), responsePayload.getTransactionAmount(),0);
        assertEquals(requestPayload.getTransactionID(), responsePayload.getTransactionID());
        assertEquals("SUCCESS", responsePayload.getTransactionResult());
        return responsePayload;
    }

    private TransactionResponseV1 assertFailedPostRequest(TransactionRequestV1 requestPayload, Response response) {
        assertEquals(200, response.getStatus());
        TransactionResponseV1 responsePayload = response.readEntity(TransactionResponseV1.class);
        assertEquals(requestPayload.getEmail(), responsePayload.getEmail());
        assertEquals(requestPayload.getTransactionAmount(), responsePayload.getTransactionAmount(),0);
        assertEquals(requestPayload.getTransactionID(), responsePayload.getTransactionID());
        assertEquals("FAIL", responsePayload.getTransactionResult());
        return responsePayload;
    }

    private TransactionRequestV1 defaultTransactionRequest(float transactionAmount) {
        return new TransactionRequestV1(new Random().nextInt() + "@email.com", transactionAmount, Math.abs(new Random().nextLong()));
    }

}
