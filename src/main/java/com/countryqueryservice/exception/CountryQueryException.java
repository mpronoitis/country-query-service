package com.countryqueryservice.exception;

import jakarta.ws.rs.core.Response;

public class CountryQueryException extends RuntimeException {

    private final Response.Status status;
    private final String error;
    private final String details;

    public CountryQueryException(Response.Status status, String error, String details) {
        super(error);
        this.status = status;
        this.error = error;
        this.details = details;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getDetails() {
        return details;
    }
}
