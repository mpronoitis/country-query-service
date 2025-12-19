package com.countryqueryservice.service;

import com.countryqueryservice.client.CountryRestClient;
import com.countryqueryservice.entity.CountryEntity;
import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.mapper.CountryMapper;
import com.countryqueryservice.model.ApiCountry;
import com.countryqueryservice.model.CountryDTO;
import com.countryqueryservice.repository.CountryRepository;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    CountryRestClient countryRestClient;

    @Mock
    CountryRepository countryRepository;

    @Mock
    CountryMapper countryMapper;

    @Mock
    RequestValidator requestValidator;

    CountryService countryService;

    @BeforeEach
    void setUp() {
        countryService = new CountryService(countryRestClient, countryRepository, countryMapper, requestValidator);
    }

    @Test
    void fetchAndPersistCountries_persistsMappedCountries() {
        ApiCountry apiCountry = buildApiCountry("gr", "Greece", "Hellenic Republic");
        CountryEntity entity = buildCountryEntity("GR");
        when(countryRestClient.getAllCountries()).thenReturn(List.of(apiCountry));
        when(countryMapper.toCountryEntity(apiCountry)).thenReturn(entity);

        countryService.fetchAndPersistCountries();

        verify(countryRepository).persist((CountryEntity) entity);
    }

    @Test
    void fetchAndPersistCountries_skipsInvalidEntities() {
        ApiCountry apiCountry = buildApiCountry("gr", "Greece", "Hellenic Republic");
        CountryEntity invalid = new CountryEntity();
        when(countryRestClient.getAllCountries()).thenReturn(List.of(apiCountry));
        when(countryMapper.toCountryEntity(apiCountry)).thenReturn(invalid);

        countryService.fetchAndPersistCountries();

        verify(countryRepository, never()).persist(Mockito.<CountryEntity>any());
    }

    @Test
    void fetchAndPersistCountries_propagatesExternalFailures() {
        when(countryRestClient.getAllCountries()).thenThrow(new RuntimeException("down"));

        CountryQueryException exception = assertThrows(CountryQueryException.class, () -> countryService.fetchAndPersistCountries());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getStatus());
    }

    @Test
    void getByCurrency_returnsMappedCountries() {
        CountryEntity entity = buildCountryEntity("GR");
        CountryDTO dto = buildCountryDTO("GR");
        when(countryRepository.findByCurrency("EUR")).thenReturn(List.of(entity));
        when(countryMapper.toCountryDTO(entity)).thenReturn(dto);

        List<CountryDTO> results = countryService.getByCurrency("EUR");

        assertEquals(1, results.size());
        assertEquals("GR", results.getFirst().getCode());
    }

    @Test
    void getByCurrency_whenNoResults_throwsCurrencyNotFound() {
        when(countryRepository.findByCurrency("EUR")).thenReturn(Collections.emptyList());

        CountryQueryException exception = assertThrows(CountryQueryException.class, () -> countryService.getByCurrency("EUR"));
        assertEquals(Response.Status.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getByCode_returnsCountry() {
        CountryEntity entity = buildCountryEntity("GR");
        CountryDTO dto = buildCountryDTO("GR");
        when(countryRepository.findByCode("GR")).thenReturn(entity);
        when(countryMapper.toCountryDTO(entity)).thenReturn(dto);

        CountryDTO result = countryService.getByCode("GR");

        assertEquals("GR", result.getCode());
    }

    @Test
    void getByCode_whenMissing_throwsCountryNotFound() {
        when(countryRepository.findByCode("GR")).thenReturn(null);

        CountryQueryException exception = assertThrows(CountryQueryException.class, () -> countryService.getByCode("GR"));
        assertEquals(Response.Status.NOT_FOUND, exception.getStatus());
    }

    private ApiCountry buildApiCountry(String code, String common, String official) {
        ApiCountry apiCountry = new ApiCountry();
        ApiCountry.Name name = new ApiCountry.Name();
        name.setCommon(common);
        name.setOfficial(official);
        apiCountry.setName(name);
        apiCountry.setCca2(code);
        return apiCountry;
    }

    private CountryEntity buildCountryEntity(String code) {
        CountryEntity entity = new CountryEntity();
        entity.setCode(code);
        entity.setCommonName("Common");
        entity.setOfficialName("Official");
        return entity;
    }

    private CountryDTO buildCountryDTO(String code) {
        CountryDTO dto = new CountryDTO();
        dto.setCode(code);
        dto.setCommonName("Common");
        dto.setOfficialName("Official");
        return dto;
    }
}
