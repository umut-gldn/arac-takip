package com.umutgldn.route_service.mapper;

import com.umutgldn.route_service.dto.response.RouteResponse;
import com.umutgldn.route_service.dto.response.RouteSummaryResponse;
import com.umutgldn.route_service.entity.Route;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteMapper {

    public RouteResponse toDetailResponse(Route route) {
        List<RouteResponse.CoordinateResponse> coordinates = route.getCoordinates().stream()
                .map(c-> new RouteResponse.CoordinateResponse(
                        c.getSequence(),
                        c.getLatitude(),
                        c.getLongitude()
                ))
                .toList();
        return new RouteResponse(
                route.getId(),
                route.getName(),
                route.getStartLatitude(),
                route.getStartLongitude(),
                route.getEndLatitude(),
                route.getEndLongitude(),
                route.getTotalDistanceMeters(),
                route.getTotalDurationSeconds(),
                route.getCreatedAt(),
                coordinates
        );
    }
    public RouteSummaryResponse toSummaryResponse(Route route) {
        return new RouteSummaryResponse(
                route.getId(),
                route.getName(),
                route.getTotalDistanceMeters(),
                route.getTotalDurationSeconds(),
                route.getCreatedAt()
        );
    }

}
