package com.countryqueryservice.service;

import com.countryqueryservice.client.CountryInfoSoapClient;
import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.model.CountryInfoResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oorsprong.websamples.ArrayOftLanguage;
import org.oorsprong.websamples.TCountryInfo;
import org.oorsprong.websamples.TLanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryInfoSoapServiceTest {

    @Mock
    CountryInfoSoapClient soapClient;

    @Mock
    RequestValidator requestValidator;

    CountryInfoSoapService soapService;

    @BeforeEach
    void setUp() {
        soapService = new CountryInfoSoapService(soapClient, requestValidator);
    }

    @Test
    void getCountryInfo_returnsMappedPayload() {
        when(soapClient.getFullCountryInfo("GR")).thenReturn(sampleInfo());

        CountryInfoResponse response = soapService.getCountryInfo("GR");

        assertEquals("GR", response.getIsoCode());
        assertEquals("Greece", response.getName());
        assertEquals(1, response.getLanguages().size());
        assertEquals("el", response.getLanguages().getFirst().getIsoCode());
    }

    @Test
    void getCountryInfo_whenMissingCountry_throwsNotFound() {
        TCountryInfo empty = new TCountryInfo();
        when(soapClient.getFullCountryInfo("ZZ")).thenReturn(empty);

        CountryQueryException exception = assertThrows(CountryQueryException.class,
                () -> soapService.getCountryInfo("ZZ"));
        assertEquals(Response.Status.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getCountryInfo_whenSoapFails_wrapsException() {
        when(soapClient.getFullCountryInfo("GR")).thenThrow(new RuntimeException("down"));

        CountryQueryException exception = assertThrows(CountryQueryException.class,
                () -> soapService.getCountryInfo("GR"));
        assertEquals(Response.Status.BAD_GATEWAY, exception.getStatus());
    }

    private TCountryInfo sampleInfo() {
        TCountryInfo info = new TCountryInfo();
        info.setSISOCode("GR");
        info.setSName("Greece");
        info.setSCapitalCity("Athens");
        info.setSPhoneCode("30");
        info.setSContinentCode("EU");
        info.setSCurrencyISOCode("EUR");
        ArrayOftLanguage languages = new ArrayOftLanguage();
        TLanguage greek = new TLanguage();
        greek.setSISOCode("el");
        greek.setSName("Greek");
        languages.getTLanguage().add(greek);
        info.setLanguages(languages);
        return info;
    }
}
