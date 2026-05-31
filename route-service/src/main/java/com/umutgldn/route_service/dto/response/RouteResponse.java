package com.umutgldn.route_service.dto.response;

import java.time.Instant;
import java.util.List;

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
    public record CoordinateResponse(
            Integer sequence,
            Double latitude,
            Double longitude
    ) {
    }
}
