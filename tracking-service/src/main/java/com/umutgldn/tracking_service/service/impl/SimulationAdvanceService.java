package com.umutgldn.tracking_service.service.impl;

import com.umutgldn.tracking_service.client.route.RouteResponse;
import com.umutgldn.tracking_service.client.route.RouteServiceClient;
import com.umutgldn.tracking_service.entity.Simulation;
import com.umutgldn.tracking_service.entity.SimulationStatus;
import com.umutgldn.tracking_service.event.LocationUpdatedEvent;
import com.umutgldn.tracking_service.event.Milestone;
import com.umutgldn.tracking_service.event.MilestoneReachedEvent;
import com.umutgldn.tracking_service.event.SimulationEventPublisher;
import com.umutgldn.tracking_service.repository.SimulationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationAdvanceService {
    private final SimulationRepository simulationRepository;
    private final RouteServiceClient routeServiceClient;
    private final SimulationEventPublisher eventPublisher;

    @Value("${simulation.step}")
    private int step;

    @Value("${simulation.max-consecutive-failures}")
    private int maxConsecutiveFailures;

    @Transactional
    public void advanceOne(Long simulationId) {
        Simulation simulation = simulationRepository.findById(simulationId).orElse(null);
        if (simulation == null || simulation.getStatus() != SimulationStatus.RUNNING) {
            return;
        }

        int lastIndex = simulation.getTotalCoordinates() - 1;
        int nextIndex = Math.min(simulation.getCurrentCoordinateIndex() + step, lastIndex);

        RouteResponse route;
        try {
            route = routeServiceClient.getRoute(simulation.getRouteId());
        } catch (FeignException e) {
            log.error("Could not fetch route {} for simulation {}", simulation.getRouteId(), simulation.getId(), e);
            simulation.setConsecutiveFailures(simulation.getConsecutiveFailures() + 1);
            if (simulation.getConsecutiveFailures() >= maxConsecutiveFailures) {
                simulation.setStatus(SimulationStatus.FAILED);
                log.error("Simulation id={} marked FAILED after {} consecutive route-fetch failures", simulation.getId(), maxConsecutiveFailures);
            }
            return;
        }
        simulation.setConsecutiveFailures(0);

        if (nextIndex >= route.coordinates().size()) {
            RouteResponse.CoordinateResponse lastCoordinate = route.coordinates().get(lastIndex);
            moveTo(simulation, lastCoordinate, lastIndex);
            completeSimulation(simulation);
            return;
        }

        RouteResponse.CoordinateResponse nextCoordinate = route.coordinates().get(nextIndex);
        moveTo(simulation, nextCoordinate, nextIndex);
        double progress = lastIndex <= 0 ? 100.0 : (double) nextIndex / lastIndex * 100.0;

        eventPublisher.publishLocationUpdated(new LocationUpdatedEvent(
                simulation.getId(),
                simulation.getVehicleName(),
                simulation.getRouteId(),
                nextIndex,
                simulation.getTotalCoordinates(),
                progress,
                nextCoordinate.latitude(),
                nextCoordinate.longitude(),
                Instant.now()
        ));

        detectAndPublishMilestone(simulation, progress);

        if (nextIndex >= lastIndex) {
            completeSimulation(simulation);
        }
    }

    private void moveTo(Simulation simulation, RouteResponse.CoordinateResponse coordinate, int index) {
        simulation.setCurrentCoordinateIndex(index);
        simulation.setCurrentLatitude(coordinate.latitude());
        simulation.setCurrentLongitude(coordinate.longitude());
    }

    private void completeSimulation(Simulation simulation) {
        simulation.setStatus(SimulationStatus.COMPLETED);
        simulation.setCompletedAt(Instant.now());
        log.info("Simulation completed: id {}", simulation.getId());
    }

    private void detectAndPublishMilestone(Simulation simulation, double progress) {
        Milestone reached = null;

        if (progress >= 100.0 && simulation.getLastMilestone() != Milestone.COMPLETED) {
            reached = Milestone.COMPLETED;
        } else if (progress >= 75.0 && isBeforeMilestone(simulation.getLastMilestone(), Milestone.THREE_QUARTERS)) {
            reached = Milestone.THREE_QUARTERS;
        } else if (progress >= 50.0 && isBeforeMilestone(simulation.getLastMilestone(), Milestone.HALFWAY)) {
            reached = Milestone.HALFWAY;
        } else if (progress >= 25.0 && isBeforeMilestone(simulation.getLastMilestone(), Milestone.QUARTER)) {
            reached = Milestone.QUARTER;
        }

        if (reached != null) {
            simulation.setLastMilestone(reached);
            eventPublisher.publishMilestoneReached(new MilestoneReachedEvent(
                    simulation.getId(),
                    simulation.getVehicleName(),
                    reached,
                    progress,
                    Instant.now()
            ));
            log.info("Milestone reached: simulation={} milestone={}", simulation.getId(), reached);
        }
    }

    private boolean isBeforeMilestone(Milestone last, Milestone target) {
        if (last == null) return true;
        return last.ordinal() < target.ordinal();
    }

}
