package com.umutgldn.tracking_service.client.route;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RouteResponse(
        Long id,
        String name,
        Double startLatitude,
        Double startLongitude,
        Double endLatitude,
        Double endLongitude,
        Double totalDistanceMeters,
        Double totalDurationSeconds,
        Instant createdAt,
        List<CoordinateResponse> coordinates
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CoordinateResponse(
            Integer sequence,
            Double latitude,
            Double longitude
    ){}
}
