package com.countryqueryservice.service;

import com.countryqueryservice.client.CountryRestClient;
import com.countryqueryservice.entity.CountryEntity;
import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.mapper.CountryMapper;
import com.countryqueryservice.model.ApiCountry;
import com.countryqueryservice.model.CountryDTO;
import com.countryqueryservice.repository.CountryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class CountryService {

    private static final Logger LOGGER = Logger.getLogger(CountryService.class);

    private final CountryRestClient countryRestClient;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final RequestValidator requestValidator;

    @Inject
    public CountryService(@RestClient CountryRestClient countryRestClient,
                          CountryRepository countryRepository,
                          CountryMapper countryMapper,
                          RequestValidator requestValidator) {
        this.countryRestClient = countryRestClient;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.requestValidator = requestValidator;
    }

    @Transactional
    public void fetchAndPersistCountries() {
        try {
            List<ApiCountry> apiCountries = countryRestClient.getAllCountries();
            List<ApiCountry> countries = apiCountries == null ? Collections.emptyList() : apiCountries;

            for (ApiCountry apiCountry : countries) {
                CountryEntity entity = countryMapper.toCountryEntity(apiCountry);
                //we do not want to persist any entity with null values so we skipping it
                if (!isPersistable(entity)) {
                    LOGGER.warnf("Skipping country with missing required attributes. Code=%s", apiCountry != null ? apiCountry.getCca2() : "unknown");
                    continue;
                }
                countryRepository.persist(entity);

            }
            LOGGER.infof("Fetched %d countries. ", countries.size());
        } catch (Exception exception) {
            LOGGER.error("Failed to fetch and persist country data.", exception);
            throw new CountryQueryException(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "External service failure",
                    "Failed to fetch country data from REST Countries API."
            );
        }
    }

    @Transactional
    public List<CountryDTO> getByCurrency(String currencyCode) {
        requestValidator.validateCurrencyCode(currencyCode);
        List<CountryDTO> results = countryRepository.findByCurrency(currencyCode).stream()
                .map(countryMapper::toCountryDTO)
                .toList();
        if (results.isEmpty()) {
            throw new CountryQueryException(
                    Response.Status.NOT_FOUND,
                    "Currency not found",
                    "No countries were found for currency code %s.".formatted(currencyCode)
            );
        }
        LOGGER.debugf("Found %d countries for currency %s.", results.size(), currencyCode);
        return results;
    }

    @Transactional
    public CountryDTO getByCode(String code) {
        requestValidator.validateCountryCode(code);
        CountryEntity entity = countryRepository.findByCode(code);
        if (entity == null) {
            throw new CountryQueryException(
                    Response.Status.NOT_FOUND,
                    "Country not found",
                    "Country with ISO code %s was not found.".formatted(code)
            );
        }
        LOGGER.debugf("Found country %s for code %s.", entity.getCommonName(), code);
        return countryMapper.toCountryDTO(entity);
    }

    private boolean isPersistable(CountryEntity entity) {
        return entity != null &&
                entity.getCode() != null &&
                entity.getCommonName() != null &&
                entity.getOfficialName() != null;
    }
}
