package com.umutgldn.route_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient osrmHttpClient(
            @Value("${osrm.base-url}") String baseUrl,
            @Value("${osrm.connect-timeout}") int connectTimeout,
            @Value("${osrm.read-timeout}") int readTimeout
    ) {
        SimpleClientHttpRequestFactory factory= new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        factory.setReadTimeout(Duration.ofMillis(readTimeout));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
