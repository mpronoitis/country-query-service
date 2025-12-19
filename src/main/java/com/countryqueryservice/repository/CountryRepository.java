package com.countryqueryservice.repository;

import com.countryqueryservice.entity.CountryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CountryRepository implements PanacheRepository<CountryEntity> {

    public CountryEntity findByCode(String code) {
        return find("SELECT c FROM CountryEntity c WHERE upper(code) = upper(?1)", code)
                .firstResult();
    }

    public List<CountryEntity> findByCurrency(String currency) {
        return find("SELECT c FROM CountryEntity c JOIN c.currencies cur WHERE upper(cur) = upper(?1)", currency)
                .list();
    }
}
