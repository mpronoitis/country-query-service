package com.countryqueryservice.service;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.VertxContextSupport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

@ApplicationScoped
@Startup
public class CountryDataLoader {

    private static final Logger LOGGER = Logger.getLogger(CountryDataLoader.class);

    private final CountryService countryService;

    public CountryDataLoader(CountryService countryService) {
        this.countryService = countryService;
    }

    @ActivateRequestContext
    public void loadData(@Observes StartupEvent event) {
        LOGGER.info("Loading country reference data from REST Countries API.");
        VertxContextSupport.subscribe( //create a new vertx context in order hibernate can open reactive transactions
                () -> countryService.fetchAndPersistCountries().toMulti(),
                multi -> multi.with(
                        unused -> LOGGER.info("Country reference data loaded."),
                        throwable -> LOGGER.error("Failed to initialize country reference data.", throwable)
                )
        );
    }
}
