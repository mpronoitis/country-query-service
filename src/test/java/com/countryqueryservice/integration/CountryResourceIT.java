package com.countryqueryservice.integration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest //load jvm and quarkus app runs
@QuarkusTestResource(RestCountriesWireMock.class)
@QuarkusTestResource(CountryInfoSoapWireMock.class)
class CountryResourceIT {

    @AfterEach
    void resetSoapStubs() {
        CountryInfoSoapWireMock.reset();
    }

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

    @Test
    void soapBackedRestEndpointReturnsFullCountryInfo() {
        CountryInfoSoapWireMock.stubCountryFound();

        given()
                .when().get("/countries/GR")
                .then()
                .statusCode(200)
                .body("isoCode", equalTo("GR"))
                .body("name", equalTo("Greece"))
                .body("capitalCity", equalTo("Athens"))
                .body("languages[0].isoCode", equalTo("el"))
                .body("languages[0].name", equalTo("Greek"));
    }

    @Test
    void soapBackedRestEndpointReturnsNotFoundWhenCountryIsMissing() {
        CountryInfoSoapWireMock.stubCountryNotFound();

        given()
                .when().get("/countries/ZZ")
                .then()
                .statusCode(404)
                .body("error", equalTo("Country not found"))
                .body("details", containsString("ZZ"));
    }

    @Test
    void soapBackedRestEndpointReturnsBadGatewayWhenSoapFails() {
        CountryInfoSoapWireMock.stubCountryInfoFailure();

        given()
                .when().get("/countries/GR")
                .then()
                .statusCode(502)
                .body("error", equalTo("Country info unavailable"))
                .body("details", containsString("Failed to retrieve country information"));
    }

    @Test
    void soapBackedRestEndpointRejectsInvalidCountryCode() {
        given()
                .when().get("/countries/gr")
                .then()
                .statusCode(400)
                .body("error", equalTo("Invalid country code"))
                .body("details", containsString("two uppercase letters"));
    }

    @Test
    void soapEndpointReturnsCountriesUsingCurrency() {
        given()
                .contentType("text/xml")
                .body(currencyRequest("EUR"))
                .when()
                .post("/services/soap/countries")
                .then()
                .statusCode(200)
                .body(containsString(">Greece<"))
                .body(containsString(">GR<"));
    }

    @Test
    void soapEndpointReturnsFaultWhenCurrencyIsInvalid() {
        given()
                .contentType("text/xml")
                .body(currencyRequest("Eur"))
                .when()
                .post("/services/soap/countries")
                .then()
                .statusCode(500)
                .body(containsString("Invalid currency code"));
    }

    @Test
    void soapEndpointReturnsFaultWhenCurrencyIsMissing() {
        given()
                .contentType("text/xml")
                .body(currencyRequest("JPY"))
                .when()
                .post("/services/soap/countries")
                .then()
                .statusCode(500)
                .body(containsString("Currency not found"));
    }

    private String currencyRequest(String currencyCode) {
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v1="http://countryqueryservice.com/soap/v1">
                   <soapenv:Header/>
                  <soapenv:Body>
                        <v1:GetCountriesByCurrency>
                           <v1:CountryReq>
                              <v1:currencyCode>%s</v1:currencyCode>
                           </v1:CountryReq>
                        </v1:GetCountriesByCurrency>
                     </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(currencyCode);
    }
}
