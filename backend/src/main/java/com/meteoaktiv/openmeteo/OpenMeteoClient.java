package com.meteoaktiv.openmeteo;

import java.net.URI;

import com.meteoaktiv.config.OpenMeteoProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;
    private final OpenMeteoProperties properties;

    public OpenMeteoClient(RestClient restClient, OpenMeteoProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public OpenMeteoResponse fetch(double lat, double lon) {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("hourly", "temperature_2m,precipitation,wind_speed_10m,cloud_cover")
                .queryParam("models", properties.getModels())
                .queryParam("forecast_days", properties.getForecastDays())
                .queryParam("timezone", properties.getTimezone())
                .queryParam("wind_speed_unit", "kmh")
                .build()
                .toUri();
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(OpenMeteoResponse.class);
    }
}
