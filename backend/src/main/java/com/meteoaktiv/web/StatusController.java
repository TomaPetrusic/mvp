package com.meteoaktiv.web;

import java.time.Instant;

import com.meteoaktiv.forecast.ForecastHourlyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final ForecastHourlyRepository forecastHourlyRepository;

    public StatusController(ForecastHourlyRepository forecastHourlyRepository) {
        this.forecastHourlyRepository = forecastHourlyRepository;
    }

    @GetMapping
    public StatusResponse status() {
        Instant lastFetch = forecastHourlyRepository.findMaxFetchedAt().orElse(null);
        return new StatusResponse(lastFetch);
    }

    public record StatusResponse(Instant lastFetch) {
    }
}
