package com.example.frota.solicitacaoTransporte;

public interface DistanceClient {
    /**
     * Returns distance in kilometers between origin and destination.
     */
    double distanceKm(double origemLat, double origemLon, double destinoLat, double destinoLon);
}
