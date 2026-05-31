package com.umutgldn.route_service.client.osrm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OsrmResponse(
        String code,
        List<Route> routes
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(
            Double distance,
            Double duration,
            Geometry geometry
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(
            String type,
            List<List<Double>> coordinates
    ) {
    }
}
