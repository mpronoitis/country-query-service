package com.countryqueryservice.resource;

import com.countryqueryservice.exception.ErrorResponse;
import com.countryqueryservice.model.CountryDTO;
import com.countryqueryservice.service.CountryService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Country Queries", description = "Query endpoints for country reference data")
public class CountryResource {

    private final CountryService countryService;

    public CountryResource(CountryService countryService) {
        this.countryService = countryService;
    }

    @GET
    @Path("currency/{currencyCode}")
    @Operation(summary = "Find countries by currency", description = "Returns all countries using the provided currency code.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Countries found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CountryDTO[].class))),
            @APIResponse(responseCode = "400", description = "Invalid currency code",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Currency not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "500", description = "Server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<CountryDTO> getByCurrency(
            @Parameter(description = "currency code", example = "EUR", required = true)
            @PathParam("currencyCode") String currencyCode) {
        return countryService.getByCurrency(currencyCode);
    }

    @GET
    @Path("code/{countryCode}")
    @Operation(summary = "Find country by country code", description = "Returns a single country, wrapped in a JSON array, for the provided code.")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Country found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CountryDTO[].class))),
            @APIResponse(responseCode = "400", description = "Invalid country code",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "500", description = "Server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<CountryDTO> getByCode(
            @Parameter(description = "country code", example = "GR", required = true)
            @PathParam("countryCode") String countryCode) {
        return List.of(countryService.getByCode(countryCode));
    }
}
