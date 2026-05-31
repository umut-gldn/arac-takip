package com.umutgldn.route_service.service;

import com.umutgldn.route_service.dto.request.CreateRouteRequest;
import com.umutgldn.route_service.dto.response.RouteResponse;
import com.umutgldn.route_service.dto.response.RouteSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {
    RouteResponse createRoute(CreateRouteRequest request);
    RouteResponse getRoute(Long id);
    Page<RouteSummaryResponse> listRoutes(Pageable pageable);
    void deleteRoute(Long id);
}
