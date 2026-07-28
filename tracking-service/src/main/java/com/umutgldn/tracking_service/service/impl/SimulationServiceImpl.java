package com.umutgldn.tracking_service.service.impl;

import com.umutgldn.common.exception.ExternalServiceException;
import com.umutgldn.common.exception.ResourceNotFoundException;
import com.umutgldn.tracking_service.client.route.RouteResponse;
import com.umutgldn.tracking_service.client.route.RouteServiceClient;
import com.umutgldn.tracking_service.dto.request.CreateSimulationRequest;
import com.umutgldn.tracking_service.dto.response.SimulationResponse;
import com.umutgldn.tracking_service.dto.response.SimulationSummaryResponse;
import com.umutgldn.tracking_service.entity.Simulation;
import com.umutgldn.tracking_service.entity.SimulationStatus;
import com.umutgldn.tracking_service.exception.InvalidSimulationStateException;
import com.umutgldn.tracking_service.mapper.SimulationMapper;
import com.umutgldn.tracking_service.repository.SimulationRepository;
import com.umutgldn.tracking_service.service.SimulationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;
    private final RouteServiceClient routeServiceClient;
    private final SimulationMapper simulationMapper;

    @Override
    public SimulationResponse createSimulation(CreateSimulationRequest request) {
        log.info("Creating simulation for vehicle: {}, route: {}", request.vehicleName(), request.routeId());

        RouteResponse route = fetchRoute(request.routeId());

        validateRouteHasCoordinates(route);
        RouteResponse.CoordinateResponse firstCoordinate = route.coordinates().get(0);

        Simulation simulation = Simulation.builder()
                .vehicleName(request.vehicleName())
                .routeId(request.routeId())
                .status(SimulationStatus.RUNNING)
                .currentCoordinateIndex(0)
                .totalCoordinates(route.coordinates().size())
                .currentLatitude(firstCoordinate.latitude())
                .currentLongitude(firstCoordinate.longitude())
                .startedAt(Instant.now())
                .build();

        Simulation saved = simulationRepository.save(simulation);
        log.info("Simulation created: id={}, status={}", saved.getId(), saved.getStatus());
        return simulationMapper.toDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SimulationResponse getSimulation(Long id) {
        Simulation simulation = findSimulationOrThrow(id);
        return simulationMapper.toDetailResponse(simulation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SimulationSummaryResponse> listSimulations(Pageable pageable) {
        return simulationRepository.findAllBy(pageable)
                .map(simulationMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public SimulationResponse pauseSimulation(Long id) {
        Simulation simulation = findSimulationOrThrow(id);

        if (simulation.getStatus() != SimulationStatus.RUNNING) {
            throw new InvalidSimulationStateException("Cannot pause simulation in state: " + simulation.getStatus());
        }
        simulation.setStatus(SimulationStatus.PAUSED);
        log.info("Simulation paused: id={}", id);
        return simulationMapper.toDetailResponse(simulation);
    }

    @Override
    @Transactional
    public SimulationResponse resumeSimulation(Long id) {
        Simulation simulation = findSimulationOrThrow(id);
        if (simulation.getStatus() != SimulationStatus.PAUSED) {
            throw new InvalidSimulationStateException(
                    "Cannot resume simulation in state: " + simulation.getStatus()
            );
        }

        simulation.setStatus(SimulationStatus.RUNNING);
        log.info("Simulation resumed: id={}", id);

        return simulationMapper.toDetailResponse(simulation);
    }

    @Override
    @Transactional
    public SimulationResponse resetSimulation(Long id) {
        Simulation simulation = findSimulationOrThrow(id);
        RouteResponse route = fetchRoute(simulation.getRouteId());
        validateRouteHasCoordinates(route);

        RouteResponse.CoordinateResponse firstCoordinate = route.coordinates().get(0);

        simulation.setStatus(SimulationStatus.RUNNING);
        simulation.setCurrentCoordinateIndex(0);
        simulation.setCurrentLatitude(firstCoordinate.latitude());
        simulation.setCurrentLongitude(firstCoordinate.longitude());
        simulation.setStartedAt(Instant.now());
        simulation.setCompletedAt(null);
        simulation.setLastMilestone(null);
        simulation.setConsecutiveFailures(0);

        log.info("Simulation reset: id={}", id);
        return simulationMapper.toDetailResponse(simulation);
    }

    @Override
    @Transactional
    public void deleteSimulation(Long id) {
        if(!simulationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Simulation not found: " + id);
        }
        simulationRepository.deleteById(id);
        log.info("Simulation deleted: id={}", id);
    }

    private Simulation findSimulationOrThrow(Long id) {
        return simulationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Simulation not found: " + id));
    }

    private RouteResponse fetchRoute(Long routeId) {
        try {
            return routeServiceClient.getRoute(routeId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Route not found: " + routeId);
        } catch (FeignException e) {
            log.error("Route service call failed", e);
            throw new ExternalServiceException("Failed to fetch route from Route Service", e);
        }
    }

    private void validateRouteHasCoordinates(RouteResponse route) {
        if (route.coordinates() == null || route.coordinates().isEmpty()) {
            throw new ExternalServiceException("Route has no coordinates: routeId= " + route.id());
        }
    }
}
