package com.umutgldn.route_service.dto.response;

import java.time.Instant;

public record RouteSummaryResponse(
        Long id,
        String name,
        Double totalDistanceMeters,
        Double totalDurationSeconds,
        Instant createdAt
) {
}
