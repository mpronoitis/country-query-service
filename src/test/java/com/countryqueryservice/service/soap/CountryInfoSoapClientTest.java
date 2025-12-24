package com.countryqueryservice.service.soap;

import com.countryqueryservice.client.CountryInfoSoapClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.xml.ws.AsyncHandler;
import jakarta.xml.ws.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oorsprong.websamples.FullCountryInfoResponse;
import org.oorsprong.websamples.TCountryInfo;
import org.oorsprong.websamples_countryinfo.CountryInfoServiceSoapType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryInfoSoapClientTest {

    @Mock
    CountryInfoServiceSoapType soapType;

    CountryInfoSoapClient soapClient;

    @BeforeEach
    void setUp() {
        soapClient = new CountryInfoSoapClient(soapType);
    }

    @Test
    void getFullCountryInfoAsync_emitsPayload() {
        TCountryInfo expected = sampleInfo();

        doAnswer(invocation -> {
            jakarta.xml.ws.AsyncHandler<FullCountryInfoResponse> handler = invocation.getArgument(1);
            var response = mock(Response.class);
            FullCountryInfoResponse payload = new FullCountryInfoResponse();
            payload.setFullCountryInfoResult(expected);
            when(response.get()).thenReturn(payload);
            handler.handleResponse(response);
            return null;
        }).when(soapType).fullCountryInfoAsync(eq("GR"), any());

        CompletableFuture<TCountryInfo> resourceFuture = new CompletableFuture<>();
        Uni<TCountryInfo> result = soapClient.getFullCountryInfoAsync("GR", resourceFuture);

        UniAssertSubscriber<TCountryInfo> subscriber = result.subscribe().withSubscriber(UniAssertSubscriber.create());
        var res = subscriber.awaitItem().getItem();
        assertNotNull(res);
        assertAll(() -> {
            assertEquals(expected, res);
            assertTrue(resourceFuture.isDone());
        });
    }

    @Test
    void getFullCountryInfoAsync_propagatesFailures() throws ExecutionException, InterruptedException {
        RuntimeException throwable = new RuntimeException("down");
        var response = mock(Response.class);
        when(response.get()).thenThrow(throwable);
        doAnswer(invocation -> {
            AsyncHandler<FullCountryInfoResponse> handler = invocation.getArgument(1);
            handler.handleResponse(response);
            return null;
        }).when(soapType).fullCountryInfoAsync(eq("GR"), any());

        CompletableFuture<TCountryInfo> resourceFuture = new CompletableFuture<>();
        Uni<TCountryInfo> result = soapClient.getFullCountryInfoAsync("GR", resourceFuture);

        UniAssertSubscriber<TCountryInfo> subscriber = result.subscribe().withSubscriber(UniAssertSubscriber.create());
        var failure = subscriber.awaitFailure().getFailure();
        assertNotNull(failure);
        assertAll(() -> {
            assertEquals(RuntimeException.class, failure.getClass());
            assertTrue(resourceFuture.isCompletedExceptionally());
            assertEquals("down", failure.getMessage());
        });


    }

    private TCountryInfo sampleInfo() {
        TCountryInfo info = new TCountryInfo();
        info.setSISOCode("GR");
        info.setSName("Greece");
        return info;
    }


}
