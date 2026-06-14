package com.umutgldn.tracking_service.dto.request;

import jakarta.validation.constraints.*;

public record CreateSimulationRequest(
        @NotBlank(message = "Vehicle name is required")
        @Size(max = 100, message = "Vehicle name must not exceed 100 characters")
        String vehicleName,

        @NotNull(message = "Route ID is required")
        @Positive(message = "Route ID must be positive")
        Long routeId
) {
}
