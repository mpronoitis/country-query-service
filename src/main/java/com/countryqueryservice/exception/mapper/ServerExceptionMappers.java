package com.countryqueryservice.exception.mapper;

import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.exception.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Provider
public class ServerExceptionMappers {

    private static final Logger LOGGER = Logger.getLogger(ServerExceptionMappers.class);


    @ServerExceptionMapper
    public Response toResponse(CountryQueryException exception) {
        if (exception.getStatus().getStatusCode() >= 500) {
            LOGGER.error(exception.getError(), exception);
        } else {
            LOGGER.debug(exception.getError(), exception);
        }
        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(exception.getError(), exception.getDetails()))
                .build();
    }

    @ServerExceptionMapper
    public Response toResponse(Throwable exception) {
        LOGGER.error("Unexpected error", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("Unexpected error", "An unexpected error occurred. Please try again later."))
                .build();
    }

}
