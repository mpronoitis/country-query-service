package com.countryqueryservice.service;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
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

    public void loadData(@Observes StartupEvent event) {
        LOGGER.info("Loading country reference data from REST Countries API.");
        countryService.fetchAndPersistCountries();
    }
}
