package com.countryqueryservice.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(name = "Country", description = "Country response payload")
public class CountryDTO {

    @Schema(description = "Commonly used country name", example = "Greece")
    private String commonName;

    @Schema(description = "Country code", example = "GR")
    private String code;

    @Schema(description = "Official country name", example = "Hellenic Republic")
    private String officialName;

    @Schema(description = "Currency codes", example = "[\"EUR\"]")
    private List<String> currencies;

    public String getCommonName() {
        return commonName;
    }

    public String getCode() {
        return code;
    }

    public String getOfficialName() {
        return officialName;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }
}
