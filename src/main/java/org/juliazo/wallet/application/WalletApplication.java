package org.juliazo.wallet.application;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * An application configuration class. Provides information about packages
 * and applies dependency injection bindings
 */
public class WalletApplication extends ResourceConfig {

    public WalletApplication() {
        register(new WalletBinder());
        packages(true, "org.juliazo.wallet");
    }
}
