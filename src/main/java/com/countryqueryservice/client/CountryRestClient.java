package com.countryqueryservice.client;

import com.countryqueryservice.model.ApiCountry;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "country-service")
public interface CountryRestClient {

    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @ClientQueryParam(name = "fields", value = "name,cca2,currencies")
    Uni<List<ApiCountry>> getAllCountries();


}
