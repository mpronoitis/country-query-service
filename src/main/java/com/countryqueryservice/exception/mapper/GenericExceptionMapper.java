package com.countryqueryservice.exception.mapper;

import com.countryqueryservice.exception.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("Unexpected error", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse("Unexpected error", "An unexpected error occurred. Please try again later."))
                .build();
    }
}
