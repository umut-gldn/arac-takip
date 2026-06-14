package com.umutgldn.route_service.client.osrm;


import com.umutgldn.common.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Locale;

@Slf4j
@Component
public class OsrmRestClient implements OsrmClient {

    private final RestClient restClient;
    private final String profile;

    public OsrmRestClient(RestClient osrmHttpClient,
                          OsrmProperties properties) {
        this.restClient = osrmHttpClient;
        this.profile = properties.profile();
    }

    @Override
    public OsrmResponse fetchRoute(double startLongitude, double startLatitude, double endLongitude, double endLatitude) {
        String path= String.format(Locale.US,"/route/v1/%s/%f,%f;%f,%f",profile,startLongitude,startLatitude,endLongitude,endLatitude);


        log.info("Calling OSRM: {}", path);
        try {
            OsrmResponse response= restClient.get()
                    .uri(uriBuilder-> uriBuilder
                            .path(path)
                            .queryParam("overview","simplified")
                            .queryParam("geometries","geojson")
                            .build())
                    .retrieve()
                    .body(OsrmResponse.class);
            if(response==null || !"Ok".equals(response.code())){
                throw new ExternalServiceException("OSRM returned invalid response : "+
                        (response==null ? "null":response.code())

                );
            }
            return response;
        }catch (RestClientException e){
            log.error("OSRM call failed",e);
            throw new ExternalServiceException("Failed to fetch route from OSRM",e);
        }
    }
}
