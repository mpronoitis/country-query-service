package com.countryqueryservice.repository;

import com.countryqueryservice.entity.CountryEntity;
import com.countryqueryservice.integration.RestCountriesWireMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
class CountryRepositoryReactiveTest {

    @Inject
    CountryRepository countryRepository;

    @Test
    @RunOnVertxContext
    void findByCodeReturnsPersistedEntity(TransactionalUniAsserter asserter) {
        CountryEntity entity = country("ES", "Spain", "Kingdom of Spain", List.of("EUR"));

        asserter.execute(() -> countryRepository.deleteAll().replaceWithVoid());
        asserter.assertThat(() -> countryRepository.persist(entity),
                persisted -> {
                    Assertions.assertNotNull(persisted.getId());
                    asserter.putData("country.id", persisted.getId());
                });

        asserter.assertThat(() -> countryRepository.findByCode("ES"), found -> {
            Assertions.assertNotNull(found);
            Assertions.assertEquals("Spain", found.getCommonName());
            Assertions.assertEquals("ES", found.getCode());
            Assertions.assertEquals(List.of("EUR"), found.getCurrencies());
        });
    }

    @Test
    @RunOnVertxContext
    void findByCurrencyReturnsAllMatches(TransactionalUniAsserter asserter) {
        CountryEntity france = country("FR", "France", "French Republic", List.of("EUR"));

        asserter.execute(() -> countryRepository.deleteAll().replaceWithVoid());
        asserter.execute(() -> countryRepository.persist(france).replaceWithVoid());

        asserter.assertThat(() -> countryRepository.findByCurrency("eur"), countries -> {
            Assertions.assertEquals(2, countries.size());
            Assertions.assertTrue(
                    countries.stream().anyMatch(country -> "GR".equals(country.getCode())));
            Assertions.assertTrue(
                    countries.stream().anyMatch(country -> "FR".equals(country.getCode())));
        });
    }

    private CountryEntity country(String code, String common, String official, List<String> currencies) {
        CountryEntity entity = new CountryEntity();
        entity.setCode(code);
        entity.setCommonName(common);
        entity.setOfficialName(official);
        entity.setCurrencies(currencies);
        return entity;
    }
}
