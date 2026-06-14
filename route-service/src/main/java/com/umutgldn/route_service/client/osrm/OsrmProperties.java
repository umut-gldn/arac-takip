package com.umutgldn.route_service.client.osrm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "osrm")
public record OsrmProperties(
        String baseUrl,
        String profile,
        int connectTimeout,
        int readTimeout
) {
}
