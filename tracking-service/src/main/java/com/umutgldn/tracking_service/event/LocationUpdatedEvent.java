package com.umutgldn.tracking_service.event;

import java.time.Instant;

public record LocationUpdatedEvent(
        Long simulationId,
        String vehicleName,
        Long routeId,
        int currentCoordinateIndex,
        int totalCoordinates,
        double progressPercentage,
        double latitude,
        double longitude,
        Instant occurredAt
) { }
