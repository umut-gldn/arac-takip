package com.umutgldn.tracking_service.mapper;

import com.umutgldn.tracking_service.dto.response.SimulationResponse;
import com.umutgldn.tracking_service.dto.response.SimulationSummaryResponse;
import com.umutgldn.tracking_service.entity.Simulation;
import org.springframework.stereotype.Component;

@Component
public class SimulationMapper {
    public SimulationResponse toDetailResponse(Simulation simulation) {
        return new SimulationResponse(
                simulation.getId(),
                simulation.getVehicleName(),
                simulation.getRouteId(),
                simulation.getStatus(),
                simulation.getCurrentCoordinateIndex(),
                simulation.getTotalCoordinates(),
                calculateProgress(simulation),
                simulation.getCurrentLatitude(),
                simulation.getCurrentLongitude(),
                simulation.getStartedAt(),
                simulation.getCompletedAt(),
                simulation.getCreatedAt(),
                simulation.getUpdatedAt()
        );
    }

    public SimulationSummaryResponse toSummaryResponse(Simulation simulation) {
        return new SimulationSummaryResponse(
                simulation.getId(),
                simulation.getVehicleName(),
                simulation.getRouteId(),
                simulation.getStatus(),
                calculateProgress(simulation),
                simulation.getCreatedAt()
        );
    }

    private Double calculateProgress(Simulation simulation) {
        Integer total = simulation.getTotalCoordinates();
        Integer current = simulation.getCurrentCoordinateIndex();

        if (total == null || total <= 1) {
            return 0.0;
        }
        double percentage = ((double) current / (total - 1)) * 100.0;
        return Math.round(percentage * 100.0) / 100.0;
    }
}
