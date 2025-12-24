package com.countryqueryservice.client;

import io.quarkiverse.cxf.annotation.CXFClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.oorsprong.websamples.TCountryInfo;
import org.oorsprong.websamples_countryinfo.CountryInfoServiceSoapType;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class CountryInfoSoapClient {

    private final CountryInfoServiceSoapType client;

    public CountryInfoSoapClient(@CXFClient("country-info") CountryInfoServiceSoapType client) {
        this.client = client;
    }

    public Uni<TCountryInfo> getFullCountryInfoAsync(String isoCode, CompletableFuture<TCountryInfo> resourceFuture) {

        client.fullCountryInfoAsync(isoCode, res -> {
            try {
                resourceFuture.complete(res.get().getFullCountryInfoResult());
            } catch (Exception e) {
                resourceFuture.completeExceptionally(e);
            }
        });

        return Uni.createFrom().future(resourceFuture);
    }

}
