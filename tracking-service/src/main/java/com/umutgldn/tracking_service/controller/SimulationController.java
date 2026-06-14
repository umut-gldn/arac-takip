package com.umutgldn.tracking_service.controller;

import com.umutgldn.tracking_service.dto.request.CreateSimulationRequest;
import com.umutgldn.tracking_service.dto.response.SimulationResponse;
import com.umutgldn.tracking_service.dto.response.SimulationSummaryResponse;
import com.umutgldn.tracking_service.service.SimulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;


    @PostMapping
    public ResponseEntity<SimulationResponse> createSimulation(
            @Valid @RequestBody CreateSimulationRequest request,
            UriComponentsBuilder uriComponentsBuilder
            ){
        SimulationResponse created=simulationService.createSimulation(request);
        URI location=uriComponentsBuilder.path("/api/v1/simulations/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimulationResponse> getSimulation(@PathVariable Long id){
        return  ResponseEntity.ok(simulationService.getSimulation(id));
    }

    @GetMapping
    public ResponseEntity<Page<SimulationSummaryResponse>> listSimulations(Pageable pageable){
        return ResponseEntity.ok(simulationService.listSimulations(pageable));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<SimulationResponse> pauseSimulation(@PathVariable Long id){
        return  ResponseEntity.ok(simulationService.pauseSimulation(id));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<SimulationResponse> resumeSimulation(@PathVariable Long id){
        return  ResponseEntity.ok(simulationService.resumeSimulation(id));
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<SimulationResponse> resetSimulation(@PathVariable Long id){
        return  ResponseEntity.ok(simulationService.resetSimulation(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSimulation(@PathVariable Long id){
        simulationService.deleteSimulation(id);
    }

}
