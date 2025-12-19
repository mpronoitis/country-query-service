package com.countryqueryservice.exception.mapper;

import com.countryqueryservice.exception.CountryQueryException;
import com.countryqueryservice.exception.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class CountryQueryExceptionMapper implements ExceptionMapper<CountryQueryException> {

    private static final Logger LOGGER = Logger.getLogger(CountryQueryExceptionMapper.class);

    @Override
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
}
