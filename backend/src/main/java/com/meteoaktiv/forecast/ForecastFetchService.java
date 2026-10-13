package com.meteoaktiv.forecast;

import java.util.List;

import com.meteoaktiv.city.City;
import com.meteoaktiv.city.CityRepository;
import com.meteoaktiv.openmeteo.OpenMeteoClient;
import com.meteoaktiv.openmeteo.OpenMeteoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ForecastFetchService {

    private static final Logger log = LoggerFactory.getLogger(ForecastFetchService.class);

    private final CityRepository cityRepository;
    private final ForecastReplaceService forecastReplaceService;
    private final OpenMeteoClient openMeteoClient;
    private final ForecastCombiner combiner = new ForecastCombiner();

    public ForecastFetchService(CityRepository cityRepository,
                                 ForecastReplaceService forecastReplaceService,
                                 OpenMeteoClient openMeteoClient) {
        this.cityRepository = cityRepository;
        this.forecastReplaceService = forecastReplaceService;
        this.openMeteoClient = openMeteoClient;
    }

    @Scheduled(initialDelay = 0, fixedRateString = "${meteoaktiv.fetch.interval-ms}")
    public void fetchAllCities() {
        List<City> cities = cityRepository.findAll();
        log.info("Starting forecast fetch for {} cities", cities.size());
        int success = 0;
        for (City city : cities) {
            try {
                fetchAndReplace(city);
                success++;
            } catch (Exception e) {
                log.error("Failed to fetch forecast for city {} ({}): {}", city.getName(), city.getId(),
                        e.getMessage(), e);
            }
        }
        log.info("Forecast fetch finished: {}/{} cities updated", success, cities.size());
    }

    public void fetchAndReplace(City city) {
        OpenMeteoResponse response = openMeteoClient.fetch(city.getLat(), city.getLon());
        List<CombinedHour> combined = combiner.combine(response);
        if (combined.isEmpty()) {
            throw new IllegalStateException("Empty forecast response for city " + city.getName());
        }
        forecastReplaceService.replace(city.getId(), combined);
    }
}
