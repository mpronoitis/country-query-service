package com.countryqueryservice.soap.model;

import com.countryqueryservice.soap.CountrySoapConstants;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoapCountry", namespace = CountrySoapConstants.NAMESPACE, propOrder = {
        "code",
        "commonName",
        "officialName",
        "currencies"
})
public class SoapCountry {

    @XmlElement(namespace = CountrySoapConstants.NAMESPACE, required = true)
    private String code;

    @XmlElement(namespace = CountrySoapConstants.NAMESPACE, required = true)
    private String commonName;

    @XmlElement(namespace = CountrySoapConstants.NAMESPACE, required = true)
    private String officialName;

    @XmlElementWrapper(name = "currencies", namespace = CountrySoapConstants.NAMESPACE)
    @XmlElement(name = "currency", namespace = CountrySoapConstants.NAMESPACE)
    private List<String> currencies = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies == null ? new ArrayList<>() : currencies;
    }
}
