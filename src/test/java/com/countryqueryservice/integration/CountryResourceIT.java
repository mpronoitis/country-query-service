package com.countryqueryservice.integration;

import com.countryqueryservice.service.CountryService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest //load jvm and quarkus app runs
@QuarkusTestResource(RestCountriesWireMock.class)
class CountryResourceIT {

    @Inject
    CountryService countryService;

    @Test
    void currencyEndpointReturnsCountries() {
        given()
                .when().get("/countries/currency/EUR")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].code", equalTo("GR"))
                .body("[0].currencies", contains("EUR"));
    }

    @Test
    void codeEndpointReturnsArrayWithSingleCountry() {
        given()
                .when().get("/countries/code/US")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].code", equalTo("US"))
                .body("[0].commonName", equalTo("United States"));
    }

    @Test
    void invalidCurrencyReturnsBadRequest() {
        given()
                .when().get("/countries/currency/Eur")
                .then()
                .statusCode(400)
                .body("error", equalTo("Invalid currency code"))
                .body("details", containsString("three uppercase letters"));
    }

    @Test
    void missingCurrencyReturnsNotFound() {
        given()
                .when().get("/countries/currency/JPY")
                .then()
                .statusCode(404)
                .body("error", equalTo("Currency not found"));
    }
}
