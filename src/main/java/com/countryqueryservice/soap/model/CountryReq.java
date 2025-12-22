package com.countryqueryservice.soap.model;

import com.countryqueryservice.soap.CountrySoapConstants;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CountryReq", namespace = CountrySoapConstants.NAMESPACE, propOrder = {
        "currencyCode"
})
@XmlRootElement(name = "CountryReq", namespace = CountrySoapConstants.NAMESPACE)
public class CountryReq {

    @XmlElement(namespace = CountrySoapConstants.NAMESPACE, required = true)
    private String currencyCode;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
