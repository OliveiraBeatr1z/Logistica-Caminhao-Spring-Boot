package com.example.frota.solicitacaoTransporte;

import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Primary
@org.springframework.beans.factory.annotation.Qualifier("simpleDistance")
public class HaversineDistanceClient implements DistanceClient {

    @Override
    public double distanceKm(double origemLat, double origemLon, double destinoLat, double destinoLon) {
        return DistanceUtil.haversineKm(origemLat, origemLon, destinoLat, destinoLon);
    }
}
