package org.juliazo.wallet.application;


import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.juliazo.wallet.domain.PlayerBalanceRepository;
import org.juliazo.wallet.domain.PlayerBalanceRepositoryImpl;
import org.juliazo.wallet.domain.TransactionHistoryRepository;
import org.juliazo.wallet.domain.TransactionHistoryRepositoryImpl;
import org.juliazo.wallet.domain.TransactionRepository;
import org.juliazo.wallet.domain.TransactionRepositoryImpl;

/**
 * A class containing interface-to-implementation bindings for dependency injection
 */
public class WalletBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(PlayerBalanceRepositoryImpl.class).to(PlayerBalanceRepository.class);
        bind(TransactionRepositoryImpl.class).to(TransactionRepository.class);
        bind(TransactionHistoryRepositoryImpl.class).to(TransactionHistoryRepository.class);
    }
}
