package com.countryqueryservice.client;

import io.quarkiverse.cxf.annotation.CXFClient;
import jakarta.enterprise.context.ApplicationScoped;
import org.oorsprong.websamples.TCountryInfo;
import org.oorsprong.websamples_countryinfo.CountryInfoServiceSoapType;

@ApplicationScoped
public class CountryInfoSoapClient {

    @CXFClient("country-info")
    CountryInfoServiceSoapType client;

    public TCountryInfo getFullCountryInfo(String isoCode) {
        return client.fullCountryInfo(isoCode);
    }
}
