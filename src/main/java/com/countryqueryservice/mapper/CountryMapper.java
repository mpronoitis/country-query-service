package com.countryqueryservice.mapper;

import com.countryqueryservice.entity.CountryEntity;
import com.countryqueryservice.model.ApiCountry;
import com.countryqueryservice.model.CountryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Mapper(
        componentModel = "jakarta-cdi",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface CountryMapper {

    CountryDTO toCountryDTO(CountryEntity countryEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commonName", expression = "java(extractCommonName(apiCountry))")
    @Mapping(target = "officialName", expression = "java(extractOfficialName(apiCountry))")
    @Mapping(target = "code", expression = "java(normalizeCountryCode(apiCountry))")
    @Mapping(target = "currencies", expression = "java(mapCurrencies(apiCountry.getCurrencies()))")
    CountryEntity toCountryEntity(ApiCountry apiCountry);

    default List<String> mapCurrencies(Map<String, ApiCountry.Currency> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            return Collections.emptyList();
        }
        return currencies.keySet().stream()
                .filter(Objects::nonNull)
                .map(code -> code.trim().toUpperCase(Locale.ROOT))
                .filter(code -> !code.isEmpty())
                .distinct()
                .toList();
    }

    default String normalizeCountryCode(ApiCountry apiCountry) {
        return apiCountry == null || apiCountry.getCca2() == null
                ? null
                : apiCountry.getCca2().trim().toUpperCase(Locale.ROOT);
    }

    default String extractCommonName(ApiCountry apiCountry) {
        return apiCountry != null && apiCountry.getName() != null ? apiCountry.getName().getCommon() : null;
    }

    default String extractOfficialName(ApiCountry apiCountry) {
        return apiCountry != null && apiCountry.getName() != null ? apiCountry.getName().getOfficial() : null;
    }
}
