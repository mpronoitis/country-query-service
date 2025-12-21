package com.countryqueryservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LanguageInfo {

    @JsonProperty("ISOCode")
    private String isoCode;

    @JsonProperty("name")
    private String name;

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
}
