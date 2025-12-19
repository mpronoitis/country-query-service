package com.countryqueryservice.service;

import com.countryqueryservice.exception.CountryQueryException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestValidatorTest {

    private RequestValidator requestValidator;

    @BeforeEach
    void setUp() {
        requestValidator = new RequestValidator();
    }

    @Test
    void validateCurrencyCode_acceptsValidCode() {
        assertDoesNotThrow(() -> requestValidator.validateCurrencyCode("EUR"));
    }

    @Test
    void validateCurrencyCode_rejectsInvalidCode() {
        CountryQueryException exception = assertThrows(CountryQueryException.class,
                () -> requestValidator.validateCurrencyCode("eu"));

        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid currency code", exception.getError());
    }

    @Test
    void validateCountryCode_acceptsValidCode() {
        assertDoesNotThrow(() -> requestValidator.validateCountryCode("GR"));
    }

    @Test
    void validateCountryCode_rejectsNullCode() {
        CountryQueryException exception = assertThrows(CountryQueryException.class,
                () -> requestValidator.validateCountryCode(null));

        assertEquals(Response.Status.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid country code", exception.getError());
    }
}
