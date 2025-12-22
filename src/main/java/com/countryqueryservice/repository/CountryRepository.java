package com.countryqueryservice.repository;

import com.countryqueryservice.entity.CountryEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CountryRepository implements PanacheRepository<CountryEntity> {

    public Uni<CountryEntity> findByCode(String code) {
        return find("SELECT DISTINCT c FROM CountryEntity c LEFT JOIN FETCH c.currencies WHERE upper(c.code) = upper(?1)", code)
                .firstResult();
    }

    public Uni<List<CountryEntity>> findByCurrency(String currency) {
        return find("SELECT c FROM CountryEntity c JOIN FETCH c.currencies cur WHERE upper(cur) = upper(?1)", currency)
                .list();
    }
}
