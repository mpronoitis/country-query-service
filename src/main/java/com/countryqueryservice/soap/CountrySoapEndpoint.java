package com.countryqueryservice.soap;

import com.countryqueryservice.model.CountryDTO;
import com.countryqueryservice.service.CountryService;
import com.countryqueryservice.soap.model.CountryReq;
import com.countryqueryservice.soap.model.CountryRes;
import com.countryqueryservice.soap.model.SoapCountry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jws.WebService;

import java.util.List;

@WebService(
        serviceName = "CountrySoapService",
        portName = "CountrySoapPort",
        targetNamespace = CountrySoapConstants.NAMESPACE,
        endpointInterface = "com.countryqueryservice.soap.CountrySoapApi"
)
@ApplicationScoped
public class CountrySoapEndpoint implements CountrySoapApi {

    private final CountryService countryService;

    @Inject
    public CountrySoapEndpoint(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public CountryRes getCountries(CountryReq request) {
        String currencyCode = request != null ? request.getCurrencyCode() : null;
        List<CountryDTO> results = countryService.getByCurrency(currencyCode);
        CountryRes response = new CountryRes();
        response.setCountries(results.stream().map(this::map).toList());
        return response;
    }

    private SoapCountry map(CountryDTO dto) {
        SoapCountry soapCountry = new SoapCountry();
        soapCountry.setCode(dto.getCode());
        soapCountry.setCommonName(dto.getCommonName());
        soapCountry.setOfficialName(dto.getOfficialName());
        soapCountry.setCurrencies(dto.getCurrencies() == null ? List.of() : dto.getCurrencies());
        return soapCountry;
    }
}
