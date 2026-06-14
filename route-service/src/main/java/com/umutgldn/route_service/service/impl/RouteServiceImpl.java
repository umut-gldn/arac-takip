package com.umutgldn.route_service.service.impl;

import com.umutgldn.common.exception.ResourceNotFoundException;
import com.umutgldn.route_service.client.osrm.OsrmClient;
import com.umutgldn.route_service.client.osrm.OsrmResponse;
import com.umutgldn.route_service.dto.request.CreateRouteRequest;
import com.umutgldn.route_service.dto.response.RouteResponse;
import com.umutgldn.route_service.dto.response.RouteSummaryResponse;
import com.umutgldn.route_service.entity.Route;
import com.umutgldn.route_service.entity.RouteCoordinate;
import com.umutgldn.route_service.mapper.RouteMapper;
import com.umutgldn.route_service.repository.RouteRepository;
import com.umutgldn.route_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final OsrmClient osrmClient;
    private final RouteMapper routeMapper;

    @Override
    @Transactional
    public RouteResponse createRoute(CreateRouteRequest request) {
        log.info("Creating route: {}", request.name());

        OsrmResponse osrmResponse = osrmClient.fetchRoute(
                request.startLongitude(), request.startLatitude(),
                request.endLongitude(), request.endLatitude()
        );
        OsrmResponse.Route osrmRoute = osrmResponse.routes().get(0);
        Route route = Route.builder()
                .name(request.name())
                .startLatitude(request.startLatitude())
                .startLongitude(request.startLongitude())
                .endLatitude(request.endLatitude())
                .endLongitude(request.endLongitude())
                .totalDistanceMeters(osrmRoute.distance())
                .totalDurationSeconds(osrmRoute.duration())
                .build();

        List<List<Double>> rawCoords = osrmRoute.geometry().coordinates();
        for (int i = 0; i < rawCoords.size(); i++) {
            List<Double> point = rawCoords.get(i);
            RouteCoordinate coord = RouteCoordinate.builder()
                    .sequence(i)
                    .longitude(point.get(0))
                    .latitude(point.get(1))
                    .build();
            route.addCoordinate(coord);
        }

        Route saved = routeRepository.save(route);
        log.info("Route created: id={}, {} coordinates", saved.getId(), saved.getCoordinates().size());

        return routeMapper.toDetailResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getRoute(Long id) {
        Route route = routeRepository.findByIdWithCoordinates(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + id));
        return routeMapper.toDetailResponse(route);
    }

    @Override
    public Page<RouteSummaryResponse> listRoutes(Pageable pageable) {
        return routeRepository.findAllBy(pageable)
                .map(routeMapper::toSummaryResponse);
    }

    @Override
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found: " + id);
        }
        routeRepository.deleteById(id);
        log.info("Route deleted: id={}", id);
    }

}
