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
import com.umutgldn.tracking_service.service.impl.SimulationAdvanceService;
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
    private final SimulationAdvanceService simulationAdvanceService;

    @Scheduled(fixedRateString = "${simulation.tick-interval-ms}")
    public void advanceRunningSimulations() {
        List<Simulation> runningSimulations = simulationRepository.findAllByStatus(SimulationStatus.RUNNING);

        if (runningSimulations.isEmpty()) {
            return;
        }
        log.debug("Advancing {} running simulations", runningSimulations.size());

        for (Simulation simulation : runningSimulations) {
            try {
                simulationAdvanceService.advanceOne(simulation.getId());
            } catch (Exception e) {
                log.error("Failed to advance simulation id={}", simulation.getId(), e);
            }
        }
    }
}
