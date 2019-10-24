package org.juliazo.wallet.service;

import com.google.common.base.Strings;
import org.juliazo.wallet.api.BalanceResponseV1;
import org.juliazo.wallet.api.TransactionHistoryV1;
import org.juliazo.wallet.api.TransactionRequestV1;
import org.juliazo.wallet.api.TransactionResponseV1;
import org.juliazo.wallet.domain.PlayerBalanceRepository;
import org.juliazo.wallet.domain.Transaction;
import org.juliazo.wallet.domain.TransactionHistoryRepository;
import org.juliazo.wallet.domain.TransactionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The public API of the credit decision solution.
 */
@Path("/")
@Singleton
public class WalletServiceV1 {

    @Inject
    private PlayerBalanceRepository playerBalanceRepository;

    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private TransactionHistoryRepository transactionHistoryRepository;

    /**
     * Handling the withdraw process for a given player
     *
     * @param transactionRequestV1 withdraw request with the amount and the player's details
     * @return the result of the transaction
     */
    @POST
    @Path("/v1/withdrawl")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionResponseV1 handleWithdrawlRequestV1(TransactionRequestV1 transactionRequestV1) {

        performArgumentChecks(transactionRequestV1);

        Transaction transaction = transactionRepository.getTransaction(transactionRequestV1.getTransactionID());

        TransactionResponseV1 response = new TransactionResponseV1();
        response.setEmail(transactionRequestV1.getEmail());
        response.setTransactionAmount(transactionRequestV1.getTransactionAmount());
        response.setTransactionID(transactionRequestV1.getTransactionID());

        if (transaction == null) {
            //todo include exception handler for player not found on get balance
            float currentBalance = playerBalanceRepository.getBalance(transactionRequestV1.getEmail());
            float newBalance = currentBalance - transactionRequestV1.getTransactionAmount();
            if (newBalance >= 0) {
                playerBalanceRepository.updateBalance(transactionRequestV1.getEmail(), -transactionRequestV1.getTransactionAmount());
                transaction = new Transaction(transactionRequestV1.getTransactionID(),
                        "WITHDRAWL", transactionRequestV1.getTransactionAmount());
                Transaction storedTransaction = transactionRepository.addTransaction(transaction);
                if (storedTransaction != null) {
                    response.setTransactionResult("FAIL");
                    response.setTransactionReason("Transaction ID is invalid.");
                    playerBalanceRepository.updateBalance(transactionRequestV1.getEmail(), transactionRequestV1.getTransactionAmount());
                    return response;
                }
                transactionHistoryRepository.persistTransaction(transactionRequestV1.getEmail(), transaction);
                response.setTransactionResult("SUCCESS");
                response.setTransactionReason("Player has sufficient balance.");

            } else {
                response.setTransactionResult("FAIL");
                response.setTransactionReason("Player has insufficient balance.");
            }

        } else {
            response.setTransactionResult("FAIL");
            response.setTransactionReason("Transaction ID is invalid.");
        }

        return response;
    }

    /**
     * Handling the deposit process for a given player
     *
     * @param transactionRequestV1 deposit request with the amount and the player's details
     * @return the result of the transaction
     */
    @POST
    @Path("/v1/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionResponseV1 handleDepositRequestV1(TransactionRequestV1 transactionRequestV1) {

        performArgumentChecks(transactionRequestV1);

        Transaction transaction = transactionRepository.getTransaction(transactionRequestV1.getTransactionID());

        TransactionResponseV1 response = new TransactionResponseV1();
        response.setEmail(transactionRequestV1.getEmail());
        response.setTransactionAmount(transactionRequestV1.getTransactionAmount());
        response.setTransactionID(transactionRequestV1.getTransactionID());

        if (transaction == null) {
            playerBalanceRepository.updateBalance(transactionRequestV1.getEmail(), transactionRequestV1.getTransactionAmount());
            transaction = new Transaction(transactionRequestV1.getTransactionID(), "DEPOSIT", transactionRequestV1.getTransactionAmount());
            Transaction storedTransaction = transactionRepository.addTransaction(transaction);
            if (storedTransaction != null) {
                response.setTransactionResult("FAIL");
                response.setTransactionReason("Transaction ID is invalid.");
                playerBalanceRepository.updateBalance(transactionRequestV1.getEmail(), -transactionRequestV1.getTransactionAmount());
                return response;
            }
            transactionHistoryRepository.persistTransaction(transactionRequestV1.getEmail(), transaction);
            response.setTransactionResult("SUCCESS");
            response.setTransactionReason("New funds credited successfully.");

        } else {
            response.setTransactionResult("FAIL");
            response.setTransactionReason("Transaction ID is invalid.");
        }
        return response;
    }

    private void performArgumentChecks(TransactionRequestV1 transactionRequestV1) {
        checkArgument(transactionRequestV1 != null);
        checkArgument(!Strings.isNullOrEmpty(transactionRequestV1.getEmail()));
        checkArgument(transactionRequestV1.getTransactionAmount() > 0);
        checkArgument(transactionRequestV1.getTransactionID() > 0);
        //todo exception handler for illegal argument
    }

    /**
     * Looking up the transaction history of a given player.
     *
     * @param email the identifier of the player
     * @return the transaction history for this customer
     */
    @GET
    @Path("/v1/history/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionHistoryV1 handleTransactionHistoryRequestV1(
            @PathParam("email") String email) {

        checkArgument(!Strings.isNullOrEmpty(email));
        Collection<Transaction> playerTransactions;

        playerTransactions = transactionHistoryRepository.lookupTransactionHistory(email);

        return new TransactionHistoryV1(email, playerTransactions);
    }

    /**
     * Looking up the account balance of a given player.
     *
     * @param email the identifier of the player
     * @return the balance for given player
     */
    @GET
    @Path("/v1/balance/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public BalanceResponseV1 handleBalanceRequestV1(
            @PathParam("email") String email) {
        //todo include exception handler for player not found no get balance
        return new BalanceResponseV1(email, playerBalanceRepository.getBalance(email));
    }
}
