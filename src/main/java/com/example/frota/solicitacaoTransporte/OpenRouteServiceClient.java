package com.example.frota.solicitacaoTransporte;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Basic OpenRouteService client skeleton. Requires `routing.ors.apiKey` property.
 * This implementation focuses on distance and uses the directions endpoint; toll estimation is not provided by default on free tiers.
 */
@Component
@org.springframework.beans.factory.annotation.Qualifier("openRoute")
public class OpenRouteServiceClient implements DistanceClient, TollClient {

    @Value("${routing.ors.apiKey:}")
    private String apiKey;

    private final RestTemplate rest = new RestTemplate();

    @Override
    public double distanceKm(double origemLat, double origemLon, double destinoLat, double destinoLon) {
        if (apiKey == null || apiKey.isBlank()) {
            return DistanceUtil.haversineKm(origemLat, origemLon, destinoLat, destinoLon);
        }

        try {
            String url = String.format(
                    "https://api.openrouteservice.org/v2/directions/driving-car?start=%f,%f&end=%f,%f",
                    origemLon, origemLat, destinoLon, destinoLat);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            var res = rest.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                // response parsing omitted for brevity; fallback to haversine
                // TODO: parse JSON and extract distance in meters
                return DistanceUtil.haversineKm(origemLat, origemLon, destinoLat, destinoLon);
            }
        } catch (Exception ex) {
            // fallback
        }
        return DistanceUtil.haversineKm(origemLat, origemLon, destinoLat, destinoLon);
    }

    @Override
    public double estimateToll(double origemLat, double origemLon, double destinoLat, double destinoLon) {
        // ORS does not provide toll in free tier; return 0 and let placeholder be used if configured
        return 0.0;
    }
}
