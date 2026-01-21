package com.countryqueryservice.camel.route;

import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.exception.ErrorResponse;
import com.countryqueryservice.service.CountryInfoSoapService;
import com.countryqueryservice.service.CountryService;
import com.countryqueryservice.soap.CountrySoapEndpoint;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class CountriesRoute extends RouteBuilder {

    private static final String CONTENT_TYPE_JSON = "application/json";

    private final CountryService countryService;
    private final CountryInfoSoapService countryInfoSoapService;

    @Inject
    public CountriesRoute(CountryService countryService,
                          CountryInfoSoapService countryInfoSoapService) {
        this.countryService = countryService;
        this.countryInfoSoapService = countryInfoSoapService;
    }

    @Override
    public void configure() {
        restConfiguration()
                .component("platform-http");

        // Map service exceptions to HTTP responses that mirror the JAX-RS error contract
        onException(CountryQueryException.class)
                .handled(true)
                .log(LoggingLevel.DEBUG, "Country query error: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.status.statusCode}"))
                .setHeader(Exchange.CONTENT_TYPE, constant(CONTENT_TYPE_JSON))
                .process(exchange -> {
                    CountryQueryException exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, CountryQueryException.class);
                    exchange.getMessage().setBody(new ErrorResponse(exception.getError(), exception.getDetails()));
                })
                .marshal().json(JsonLibrary.Jackson);

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Unexpected error while fetching countries by currency: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant(CONTENT_TYPE_JSON))
                .process(exchange -> exchange.getMessage().setBody(
                        new ErrorResponse("Unexpected error", "An unexpected error occurred while fetching countries.")))
                .marshal().json(JsonLibrary.Jackson);

        // Fetch countries by currency code
        from("platform-http:/camel/rest/countries/currency/{currencyCode}?httpMethodRestrict=GET")
                .routeId("fetchCountriesByCurrencyCode")
                .log(LoggingLevel.INFO, "Fetching countries for currency code: ${header.currencyCode}")
                .bean(countryService, "getByCurrency")
                .setHeader(Exchange.CONTENT_TYPE, constant(CONTENT_TYPE_JSON))
                .marshal().json(JsonLibrary.Jackson);

        // Fetch countries by country code
        from("platform-http:/camel/rest/countries/code/{countryCode}?httpMethodRestrict=GET")
                .routeId("fetchCountryByCode")
                .log(LoggingLevel.INFO, "Fetching countries for country code: ${header.countryCode}")
                .setBody(simple("${header.countryCode}"))
                .bean(countryService, "getByCode")
                .setHeader(Exchange.CONTENT_TYPE, constant(CONTENT_TYPE_JSON))
                .marshal().json(JsonLibrary.Jackson);

       // Fetch country info from SOAP service and return them as SOAP
        from("cxf:/camel/soap/countries"
         + "?serviceClass=com.countryqueryservice.soap.CountrySoapApi"
        + "&dataFormat=POJO").routeId("fetchCountryInfoFromSoap")
                .bean(CountrySoapEndpoint.class, "getCountries");
    }
}
