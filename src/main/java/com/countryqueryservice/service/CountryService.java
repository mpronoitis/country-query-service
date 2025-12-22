package com.countryqueryservice.service;

import com.countryqueryservice.client.CountryRestClient;
import com.countryqueryservice.entity.CountryEntity;
import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.mapper.CountryMapper;
import com.countryqueryservice.model.ApiCountry;
import com.countryqueryservice.model.CountryDTO;
import com.countryqueryservice.repository.CountryRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.ArrayList;
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

    @WithTransaction
    public Uni<Void> fetchAndPersistCountries() {
        return countryRestClient.getAllCountries()
                .onItem().ifNull().continueWith(Collections::emptyList)
                .flatMap(countries -> {
                    List<CountryEntity> persistableEntities = toPersistableEntities(countries);
                    return persistEntities(persistableEntities)
                            .invoke(unused -> LOGGER.infof("Fetched %d countries. Persisting %d valid entries.", countries.size(), persistableEntities.size()))
                            .replaceWithVoid();
                })
                .onFailure().invoke(throwable -> LOGGER.error("Failed to fetch and persist country data.", throwable))
                .onFailure().transform(throwable -> {
                    if (throwable instanceof CountryQueryException exception) {
                        return exception;
                    }
                    return new CountryQueryException(
                            Response.Status.INTERNAL_SERVER_ERROR,
                            "External service failure",
                            "Failed to fetch country data from REST Countries API."
                    );
                });
    }

    @WithSession
    public Uni<List<CountryDTO>> getByCurrency(String currencyCode) {
        requestValidator.validateCurrencyCode(currencyCode);
        return countryRepository.findByCurrency(currencyCode)
                .map(entities -> entities.stream()
                        .map(countryMapper::toCountryDTO).toList())
                .flatMap(dtoList -> {
                    if (dtoList.isEmpty()) {
                        return Uni.createFrom().failure(new CountryQueryException(
                                Response.Status.NOT_FOUND,
                                "Currency not found",
                                "No countries with currency %s were found.".formatted(currencyCode)
                                )
                        );
                    }
                    return Uni.createFrom().item(dtoList);
                })
                .invoke(dtoList -> LOGGER.debugf("Found %d countries for currency %s.", dtoList.size(), currencyCode));

    }
    @WithSession
    public Uni<CountryDTO> getByCode(String code) {
        requestValidator.validateCountryCode(code);
        return countryRepository.findByCode(code)
                .onItem().ifNull().failWith(() -> new CountryQueryException(
                        Response.Status.NOT_FOUND,
                        "Country not found",
                        "Country with ISO code %s was not found.".formatted(code)
                ))
                .map(countryMapper::toCountryDTO)
                .invoke(dto -> LOGGER.debugf("Found country %s.", dto.getCommonName()));
    }

    private Uni<Void> persistEntities(List<CountryEntity> entities) {
        if (entities.isEmpty()) {
            return Uni.createFrom().voidItem();
        }
        return Multi.createFrom().iterable(entities)
                .onItem().transformToUniAndConcatenate(countryRepository::persist)
                .collect().asList()
                .replaceWithVoid();
    }

    private List<CountryEntity> toPersistableEntities(List<ApiCountry> apiCountries) {
        List<CountryEntity> persistable = new ArrayList<>();
        for (ApiCountry apiCountry : apiCountries) {
            CountryEntity entity = countryMapper.toCountryEntity(apiCountry);
            if (isPersistable(entity)) {
                persistable.add(entity);
            } else {
                LOGGER.warnf("Skipping country with missing required attributes. Code=%s", apiCountry != null ? apiCountry.getCca2() : "unknown");
            }
        }
        return persistable;
    }

    private boolean isPersistable(CountryEntity entity) {
        return entity != null &&
                entity.getCode() != null &&
                entity.getCommonName() != null &&
                entity.getOfficialName() != null;
    }
}
