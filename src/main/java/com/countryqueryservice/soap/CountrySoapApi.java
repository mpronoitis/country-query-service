package com.countryqueryservice.soap;

import com.countryqueryservice.soap.model.CountryReq;
import com.countryqueryservice.soap.model.CountryRes;
import io.smallrye.mutiny.Uni;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(targetNamespace = CountrySoapConstants.NAMESPACE, name = "CountrySoapApi")
public interface CountrySoapApi {

    @WebMethod(operationName = "GetCountriesByCurrency")
    @WebResult(name = "CountryRes", targetNamespace = CountrySoapConstants.NAMESPACE)
    CountryRes getCountries(
            @WebParam(name = "CountryReq", targetNamespace = CountrySoapConstants.NAMESPACE)
            CountryReq request
    );
}
