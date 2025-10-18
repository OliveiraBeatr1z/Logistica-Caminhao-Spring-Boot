package com.example.frota.solicitacaoTransporte;

public interface TollClient {
    /**
     * Returns estimated toll cost for the route (local currency units)
     */
    double estimateToll(double origemLat, double origemLon, double destinoLat, double destinoLon);
}
