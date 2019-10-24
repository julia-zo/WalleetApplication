package org.juliazo.wallet.application;


import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.juliazo.wallet.domain.*;

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
