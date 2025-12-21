package com.countryqueryservice.soap.model;

import com.countryqueryservice.soap.CountrySoapConstants;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CountryRes", namespace = CountrySoapConstants.NAMESPACE, propOrder = {
        "countries"
})
@XmlRootElement(name = "CountryRes", namespace = CountrySoapConstants.NAMESPACE)
public class CountryRes {

    @XmlElementWrapper(name = "countries", namespace = CountrySoapConstants.NAMESPACE)
    @XmlElement(name = "country", namespace = CountrySoapConstants.NAMESPACE)
    private List<SoapCountry> countries = new ArrayList<>();

    public List<SoapCountry> getCountries() {
        return countries;
    }

    public void setCountries(List<SoapCountry> countries) {
        this.countries = countries == null ? new ArrayList<>() : countries;
    }
}
