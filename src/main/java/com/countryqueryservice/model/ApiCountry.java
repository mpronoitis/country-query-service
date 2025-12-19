package com.countryqueryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiCountry {

    private Name name;
    private String cca2;
    private Map<String, Currency> currencies;

    public Map<String, Currency> getCurrencies() {
        return currencies;
    }

    public String getCca2() {
        return cca2;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setCca2(String cca2) {
        this.cca2 = cca2;
    }

    public void setCurrencies(Map<String, Currency> currencies) {
        this.currencies = currencies;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        private String common;
        private String official;

        public String getCommon() {
            return common;
        }

        public String getOfficial() {
            return official;
        }

        public void setCommon(String common) {
            this.common = common;
        }

        public void setOfficial(String official) {
            this.official = official;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Currency {
        private String name;
        private String symbol;

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }
    }
}
