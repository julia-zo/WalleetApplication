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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.BadRequestException;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The public API of the wallet application.
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
     * Handling the withdrawl process for a given player.
     *
     * A transaction is accepted when all it's parameters are valid.
     * An accepted transaction can either be successful or fail. The information regarding it's outcome
     * will be available in the response payload.
     *
     * Withdrawl transactions will only be successful when the remaining balance is equal or grater than zero.
     * A transaction which does not have an unique identifier wil fail.
     *
     * @param transactionRequestV1 withdraw request with the amount and the player's details
     * @return the result of the transaction with http status 200 for accepted transactions
     * @throws {@link BadRequestException} with http status 400 for malformed request according to {@link #performArgumentChecks}
     * @throws @throws {@link NotFoundException} with http status 404 when the given player is not present in the repository
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
            float currentBalance = playerBalanceRepository.getBalance(transactionRequestV1.getEmail());
            float newBalance = currentBalance - transactionRequestV1.getTransactionAmount();
            if (newBalance >= 0) {
                playerBalanceRepository.addAmountToBalance(transactionRequestV1.getEmail(), -transactionRequestV1.getTransactionAmount());
                transaction = new Transaction(transactionRequestV1.getTransactionID(),
                        "WITHDRAWL", transactionRequestV1.getTransactionAmount());
                Transaction storedTransaction = transactionRepository.addTransaction(transaction);
                if (storedTransaction != null) {
                    response.setTransactionResult("FAIL");
                    response.setTransactionReason("Transaction ID is invalid.");
                    playerBalanceRepository.addAmountToBalance(transactionRequestV1.getEmail(), transactionRequestV1.getTransactionAmount());
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
     * Handling the deposit process for a given player.
     * A player only exists in the system after it's first successful deposit transaction.
     *
     * A transaction is accepted when all it's parameters are valid.
     * An accepted transaction can either be successful or fail. The information regarding it's outcome
     * will be available in the response payload.
     *
     * A transaction which does not have an unique identifier wil fail.
     * A successful transaction will increase the player's balance.
     *
     * @param transactionRequestV1 deposit request with the amount and the player's details
     * @return the result of the transaction with http status 200 for accepted transactions
     * @throws {@link BadRequestException} with http status 400 for malformed request according to {@link #performArgumentChecks}
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
            playerBalanceRepository.addAmountToBalance(transactionRequestV1.getEmail(), transactionRequestV1.getTransactionAmount());
            transaction = new Transaction(transactionRequestV1.getTransactionID(), "DEPOSIT", transactionRequestV1.getTransactionAmount());
            Transaction storedTransaction = transactionRepository.addTransaction(transaction);
            if (storedTransaction != null) {
                response.setTransactionResult("FAIL");
                response.setTransactionReason("Transaction ID is invalid.");
                playerBalanceRepository.addAmountToBalance(transactionRequestV1.getEmail(), -transactionRequestV1.getTransactionAmount());
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
        try {
            checkArgument(transactionRequestV1 != null);
            checkArgument(!Strings.isNullOrEmpty(transactionRequestV1.getEmail()));
            checkArgument(transactionRequestV1.getTransactionAmount() > 0);
            checkArgument(transactionRequestV1.getTransactionID() > 0);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(exception.getCause());
        }
    }

    /**
     * Looking up the transaction history of a given player.
     *
     * @param email the identifier of the player
     * @return the transaction history for this player with http status 200
     * @throws {@link NotFoundException} with http status 404 when the given player is not present in the repository
     */
    @GET
    @Path("/v1/history/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionHistoryV1 handleTransactionHistoryRequestV1(
            @PathParam("email") String email) {

        Collection<Transaction> playerTransactions = transactionHistoryRepository.lookupTransactionHistory(email);
        if (playerTransactions.isEmpty()) {
            throw new NotFoundException("Player not Found");
        }

        return new TransactionHistoryV1(email, playerTransactions);
    }

    /**
     * Looking up the account balance of a given player.
     *
     * @param email the identifier of the player
     * @return the balance for given player with http status 200
     * @throws {@link NotFoundException} with http status 404 when the given player is not present in the repository
     *
     */
    @GET
    @Path("/v1/balance/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public BalanceResponseV1 handleBalanceRequestV1(
            @PathParam("email") String email) {
        return new BalanceResponseV1(email, playerBalanceRepository.getBalance(email));
    }
}
