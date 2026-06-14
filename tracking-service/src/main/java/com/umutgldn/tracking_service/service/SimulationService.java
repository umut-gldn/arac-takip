package com.umutgldn.tracking_service.service;

import com.umutgldn.tracking_service.dto.request.CreateSimulationRequest;
import com.umutgldn.tracking_service.dto.response.SimulationResponse;
import com.umutgldn.tracking_service.dto.response.SimulationSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SimulationService {

    SimulationResponse createSimulation(CreateSimulationRequest request);

    SimulationResponse getSimulation(Long id);

    Page<SimulationSummaryResponse> listSimulations(Pageable pageable);

    SimulationResponse pauseSimulation(Long id);

    SimulationResponse resumeSimulation(Long id);

    SimulationResponse resetSimulation(Long id);

    void deleteSimulation(Long id);
}
