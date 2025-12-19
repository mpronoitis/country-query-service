package com.countryqueryservice.service;

import com.countryqueryservice.exception.CountryQueryException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.Objects;
import java.util.regex.Pattern;

@ApplicationScoped
public class RequestValidator {

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Z]{3}$");
    private static final Pattern COUNTRY_PATTERN = Pattern.compile("^[A-Z]{2}$");

    public void validateCurrencyCode(String currencyCode) {
        validate(currencyCode, CURRENCY_PATTERN, "Invalid currency code", "Currency code must be three uppercase letters.");
    }

    public void validateCountryCode(String countryCode) {
        validate(countryCode, COUNTRY_PATTERN, "Invalid country code", "Country code must be two uppercase letters.");
    }

    private void validate(String value, Pattern pattern, String error, String details) {
        if (Objects.isNull(value) || !pattern.matcher(value).matches()) {
            throw new CountryQueryException(Response.Status.BAD_REQUEST, error, details);
        }
    }
}
