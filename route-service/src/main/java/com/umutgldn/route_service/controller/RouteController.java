package com.umutgldn.route_service.controller;

import com.umutgldn.route_service.dto.request.CreateRouteRequest;
import com.umutgldn.route_service.dto.response.RouteResponse;
import com.umutgldn.route_service.dto.response.RouteSummaryResponse;
import com.umutgldn.route_service.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
            @Valid  @RequestBody CreateRouteRequest request,
            UriComponentsBuilder uriBuilder) {
        RouteResponse created= routeService.createRoute(request);
        URI location=uriBuilder.path("/api/v1/routes/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRoute(id));
    }

    @GetMapping
    public ResponseEntity<Page<RouteSummaryResponse>> listRoutes(Pageable pageable) {
        return ResponseEntity.ok(routeService.listRoutes(pageable));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
    }

}
