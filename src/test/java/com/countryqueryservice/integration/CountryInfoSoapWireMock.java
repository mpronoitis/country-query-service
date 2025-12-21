package com.countryqueryservice.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.jboss.logging.Logger;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class CountryInfoSoapWireMock implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = Logger.getLogger(CountryInfoSoapWireMock.class);
    private static final String SOAP_PATH = "/org.oorsprong.websamples_countryinfo.countryinfoservicesoaptype";

    private static WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        LOGGER.infof("SOAP WireMock started on %s", wireMockServer.baseUrl());
        return Map.of("quarkus.cxf.client.country-info.client-endpoint-url", wireMockServer.baseUrl() + SOAP_PATH);
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

    public static void reset() {
        if (wireMockServer != null) {
            wireMockServer.resetAll();
        }
    }

    public static void stubCountryFound() {
        ensureServer();
        wireMockServer.stubFor(post(urlEqualTo(SOAP_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(successResponse())));
    }

    public static void stubCountryNotFound() {
        ensureServer();
        wireMockServer.stubFor(post(urlEqualTo(SOAP_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(notFoundResponse())));
    }

    public static void stubCountryInfoFailure() {
        ensureServer();
        wireMockServer.stubFor(post(urlEqualTo(SOAP_PATH))
                .willReturn(aResponse().withStatus(500)));
    }

    private static void ensureServer() {
        if (wireMockServer == null) {
            throw new IllegalStateException("WireMock server not started");
        }
    }

    private static String successResponse() {
        return """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <FullCountryInfoResponse xmlns="http://www.oorsprong.org/websamples.countryinfo">
                      <FullCountryInfoResult>
                        <sISOCode>GR</sISOCode>
                        <sName>Greece</sName>
                        <sCapitalCity>Athens</sCapitalCity>
                        <sPhoneCode>30</sPhoneCode>
                        <sContinentCode>EU</sContinentCode>
                        <sCurrencyISOCode>EUR</sCurrencyISOCode>
                        <Languages>
                          <tLanguage>
                            <sISOCode>el</sISOCode>
                            <sName>Greek</sName>
                          </tLanguage>
                        </Languages>
                      </FullCountryInfoResult>
                    </FullCountryInfoResponse>
                  </soap:Body>
                </soap:Envelope>
                """;
    }

    private static String notFoundResponse() {
        return """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <FullCountryInfoResponse xmlns="http://www.oorsprong.org/websamples.countryinfo">
                      <FullCountryInfoResult>
                        <sISOCode></sISOCode>
                        <sName></sName>
                        <sCapitalCity></sCapitalCity>
                        <sPhoneCode></sPhoneCode>
                        <sContinentCode></sContinentCode>
                        <sCurrencyISOCode></sCurrencyISOCode>
                        <Languages/>
                      </FullCountryInfoResult>
                    </FullCountryInfoResponse>
                  </soap:Body>
                </soap:Envelope>
                """;
    }
}
