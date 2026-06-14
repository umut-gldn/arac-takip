package com.umutgldn.tracking_service.dto.response;

import com.umutgldn.tracking_service.entity.SimulationStatus;

import java.time.Instant;

public record SimulationResponse(
        Long id,
        String vehicleName,
        Long routeId,
        SimulationStatus status,
        Integer currentCoordinateIndex,
        Integer totalCoordinates,
        Double progressPercentage,
        Double currentLatitude,
        Double currentLongitude,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
