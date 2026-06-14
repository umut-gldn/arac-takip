package com.umutgldn.route_service.config;


import com.umutgldn.route_service.client.osrm.OsrmProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient osrmHttpClient(OsrmProperties osrmProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(osrmProperties.connectTimeout()));
        factory.setReadTimeout(Duration.ofMillis(osrmProperties.readTimeout()));

        return RestClient.builder()
                .baseUrl(osrmProperties.baseUrl())
                .requestFactory(factory)
                .build();
    }
}
