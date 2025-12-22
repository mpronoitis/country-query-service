package com.countryqueryservice.service;

import com.countryqueryservice.client.CountryInfoSoapClient;
import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.model.CountryInfoResponse;
import com.countryqueryservice.model.LanguageInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.oorsprong.websamples.ArrayOftLanguage;
import org.oorsprong.websamples.TCountryInfo;
import org.oorsprong.websamples.TLanguage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class CountryInfoSoapService {

    private static final Logger LOGGER = Logger.getLogger(CountryInfoSoapService.class);

    private final CountryInfoSoapClient soapClient;
    private final RequestValidator requestValidator;

    @Inject
    public CountryInfoSoapService(CountryInfoSoapClient soapClient, RequestValidator requestValidator) {
        this.soapClient = soapClient;
        this.requestValidator = requestValidator;
    }

    public CountryInfoResponse getCountryInfo(String countryCode) {
        requestValidator.validateCountryCode(countryCode);
        try {
            TCountryInfo countryInfo = soapClient.getFullCountryInfo(countryCode);
            if (countryInfo == null || isBlank(countryInfo.getSISOCode())) {
                throw new CountryQueryException(Response.Status.NOT_FOUND,
                        "Country not found",
                        "Country with ISO code %s was not found in SOAP service.".formatted(countryCode));
            }
            LOGGER.debugf("Retrieved country %s from SOAP service.", countryInfo.getSName());
            return mapToResponse(countryInfo);
        } catch (CountryQueryException exception) {
            throw exception;
        } catch (Exception exception) {
            LOGGER.errorf(exception, "Failed to fetch country %s from SOAP service.", countryCode);
            throw new CountryQueryException(
                    Response.Status.BAD_GATEWAY,
                    "Country info unavailable",
                    "Failed to retrieve country information from SOAP service."
            );
        }
    }

    private CountryInfoResponse mapToResponse(TCountryInfo info) {
        CountryInfoResponse response = new CountryInfoResponse();
        response.setIsoCode(info.getSISOCode());
        response.setName(info.getSName());
        response.setCapitalCity(info.getSCapitalCity());
        response.setPhoneCode(info.getSPhoneCode());
        response.setContinentCode(info.getSContinentCode());
        response.setCurrencyCode(info.getSCurrencyISOCode());
        response.setLanguages(mapLanguages(info.getLanguages()));
        return response;
    }

    private List<LanguageInfo> mapLanguages(ArrayOftLanguage languages) {
        if (languages == null || languages.getTLanguage() == null) {
            return Collections.emptyList();
        }
        return languages.getTLanguage().stream()
                .filter(Objects::nonNull)
                .map(this::mapLanguage)
                .toList();
    }

    private LanguageInfo mapLanguage(TLanguage language) {
        LanguageInfo languageInfo = new LanguageInfo();
        languageInfo.setIsoCode(language.getSISOCode());
        languageInfo.setName(language.getSName());
        return languageInfo;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
