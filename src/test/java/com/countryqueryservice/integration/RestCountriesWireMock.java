package com.countryqueryservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.jboss.logging.Logger;
import java.util.Map;

public class RestCountriesWireMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/all"))
                .withQueryParam("fields", WireMock.equalTo("name,cca2,currencies"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {
                                    "name": {"common": "Greece", "official": "Hellenic Republic"},
                                    "cca2": "GR",
                                    "currencies": {"EUR": {"name": "Euro", "symbol": "EUR"}}
                                  },
                                  {
                                    "name": {"common": "United States", "official": "United States of America"},
                                    "cca2": "US",
                                    "currencies": {"USD": {"name": "United States dollar", "symbol": "$"}}
                                  }
                                ]
                                """)));
        return Map.of("quarkus.rest-client.country-service.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
