package com.example.frota.solicitacaoTransporte;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Primary
@org.springframework.beans.factory.annotation.Qualifier("simpleToll")
public class PlaceholderTollClient implements TollClient {

    @Value("${routing.toll.perKm:0.05}")
    private double tollPerKm;

    @Override
    public double estimateToll(double origemLat, double origemLon, double destinoLat, double destinoLon) {
        double dist = DistanceUtil.haversineKm(origemLat, origemLon, destinoLat, destinoLon);
        return dist * tollPerKm;
    }
}
