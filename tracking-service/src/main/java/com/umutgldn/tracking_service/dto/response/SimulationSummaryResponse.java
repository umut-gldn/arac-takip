package com.umutgldn.tracking_service.dto.response;

import com.umutgldn.tracking_service.entity.SimulationStatus;

import java.time.Instant;

public record SimulationSummaryResponse(
        Long id,
        String vehicleName,
        Long routeId,
        SimulationStatus status,
        Double progressPercentage,
        Instant createdAt
) {
}
