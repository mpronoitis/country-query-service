package com.countryqueryservice.exception;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Standard error response")
public record ErrorResponse(
        @Schema(description = "Short error summary", example = "Invalid currency code") String error,
        @Schema(description = "Helpful error details", example = "Currency code must be three uppercase letters.") String details) {
}
