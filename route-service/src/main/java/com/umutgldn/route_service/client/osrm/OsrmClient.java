package com.umutgldn.route_service.client.osrm;

public interface OsrmClient {
    OsrmResponse fetchRoute(double startLongitude, double startLatitude, double endLongitude, double endLatitude);
}
