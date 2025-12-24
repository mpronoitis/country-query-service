package com.countryqueryservice.soap;

import com.countryqueryservice.model.CountryDTO;
import com.countryqueryservice.service.CountryService;
import com.countryqueryservice.soap.model.CountryReq;
import com.countryqueryservice.soap.model.CountryRes;
import com.countryqueryservice.soap.model.SoapCountry;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
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
    Vertx vertx;

    @Inject
    public CountrySoapEndpoint(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public CountryRes getCountries(CountryReq request) {
        String currencyCode = request != null ? request.getCurrencyCode() : null;
        Uni<CountryRes> uni = countryService.getByCurrency(currencyCode)
                .map(list -> {
                    CountryRes res = new CountryRes();
                    res.setCountries(list.stream().map(this::map).toList());
                    return res;
                });
        //rework in order to support vertx event loop but finally blocking it. We cannot use Uni to JAXB types for the soap endpoint.
        return Uni.createFrom().<CountryRes>emitter(emitter -> {
            vertx.runOnContext(v -> uni.subscribe().with(emitter::complete, emitter::fail));
        }).await().indefinitely();
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
