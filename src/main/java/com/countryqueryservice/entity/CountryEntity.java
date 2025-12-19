package com.countryqueryservice.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "country_entity")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "common_name", nullable = false, length = 250)
    private String commonName;

    @Column(name = "code", nullable = false, unique = true, length = 2)
    private String code;

    @Column(name = "official_name", nullable = false, length = 250)
    private String officialName;

    @ElementCollection
    @CollectionTable(name = "country_currencies", joinColumns = @JoinColumn(name = "country_id"))
    @Column(name = "currency", nullable = false, length = 3)
    private List<String> currencies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getCode() {
        return code;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }
}
