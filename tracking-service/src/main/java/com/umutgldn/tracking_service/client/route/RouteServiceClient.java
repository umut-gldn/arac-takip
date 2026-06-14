package com.umutgldn.tracking_service.client.route;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "route-service",url = "${route-service.url}")
public interface RouteServiceClient {

    @GetMapping("/api/v1/routes/{id}")
    RouteResponse getRoute(@PathVariable Long id);
}
