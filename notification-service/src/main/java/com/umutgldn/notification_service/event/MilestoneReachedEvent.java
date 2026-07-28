package com.umutgldn.notification_service.event;

import java.time.Instant;

public record MilestoneReachedEvent(
        Long simulationId,
        String vehicleName,
        Milestone milestone,
        double progressPercentage,
        Instant occurredAt
) {}
