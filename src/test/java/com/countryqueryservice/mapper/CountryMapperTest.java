package com.countryqueryservice.mapper;

import com.countryqueryservice.entity.CountryEntity;
import com.countryqueryservice.model.ApiCountry;
import com.countryqueryservice.model.CountryDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class CountryMapperTest {

    private final CountryMapper mapper = Mappers.getMapper(CountryMapper.class);

    @Test
    void toCountryDTO_mapsAllFields() {
        CountryEntity entity = new CountryEntity();
        entity.setCommonName("Greece");
        entity.setOfficialName("Hellenic Republic");
        entity.setCode("GR");
        entity.setCurrencies(List.of("EUR"));

        CountryDTO dto = mapper.toCountryDTO(entity);

        assertEquals("Greece", dto.getCommonName());
        assertEquals("Hellenic Republic", dto.getOfficialName());
        assertEquals("GR", dto.getCode());
        assertIterableEquals(List.of("EUR"), dto.getCurrencies());
    }

    @Test
    void toCountryEntity_extractsCurrencyCodesAndUppercasesCode() {
        ApiCountry apiCountry = new ApiCountry();
        ApiCountry.Name name = new ApiCountry.Name();
        name.setCommon("Greece");
        name.setOfficial("Hellenic Republic");
        apiCountry.setName(name);
        apiCountry.setCca2("gr");
        apiCountry.setCurrencies(Map.of("eur", new ApiCountry.Currency()));

        CountryEntity entity = mapper.toCountryEntity(apiCountry);

        assertEquals("GR", entity.getCode());
        assertEquals("Greece", entity.getCommonName());
        assertEquals("Hellenic Republic", entity.getOfficialName());
        assertIterableEquals(List.of("EUR"), entity.getCurrencies());
    }
}
