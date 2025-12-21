package com.countryqueryservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CountryInfoResponse {

    @JsonProperty("ISOCode")
    private String isoCode;

    @JsonProperty("name")
    private String name;

    @JsonProperty("capitalCity")
    private String capitalCity;

    @JsonProperty("phoneCode")
    private String phoneCode;

    @JsonProperty("continentCode")
    private String continentCode;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("languages")
    private List<LanguageInfo> languages = new ArrayList<>();

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<LanguageInfo> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageInfo> languages) {
        this.languages = languages == null ? new ArrayList<>() : languages;
    }
}
