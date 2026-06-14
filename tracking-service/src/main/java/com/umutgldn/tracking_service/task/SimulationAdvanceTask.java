package com.umutgldn.tracking_service.task;

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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimulationAdvanceTask {

    private final SimulationRepository simulationRepository;
    private final RouteServiceClient routeServiceClient;
    private final SimulationEventPublisher eventPublisher;

    @Value("${simulation.step}")
    private int step;

    @Scheduled(fixedRateString = "${simulation.tick-interval-ms}")
    @Transactional
    public void advanceRunningSimulations() {
        List<Simulation> runningSimulations = simulationRepository.findAllByStatus(SimulationStatus.RUNNING);

        if (runningSimulations.isEmpty()) {
            return;
        }
        log.debug("Advancing {} running simulations", runningSimulations.size());

        for (Simulation simulation : runningSimulations) {
            try {
                advanceOne(simulation);
            } catch (Exception e) {
                log.error("Failed to advance simulation id={}", simulation.getId(), e);
            }
        }

    }

    private void advanceOne(Simulation simulation) {
        int lastIndex = simulation.getTotalCoordinates() - 1;
        int nextIndex = Math.min(simulation.getCurrentCoordinateIndex() + step, lastIndex);

        RouteResponse route;
        try {
            route = routeServiceClient.getRoute(simulation.getRouteId());
        } catch (FeignException e) {
            log.error("Could not fetch route {} for simulation {}", simulation.getRouteId(), simulation.getId(), e);
            return;
        }

        if (nextIndex >= route.coordinates().size()) {
            completeSimulation(simulation);
            return;
        }

        RouteResponse.CoordinateResponse nextCoordinate = route.coordinates().get(nextIndex);
        simulation.setCurrentCoordinateIndex(nextIndex);
        simulation.setCurrentLatitude(nextCoordinate.latitude());
        simulation.setCurrentLongitude(nextCoordinate.longitude());
        double progress = (double) nextIndex / lastIndex * 100.0;

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

        detectAndPublishMilestone(simulation,progress);

        if (nextIndex >= lastIndex) {
            completeSimulation(simulation);
        }
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
