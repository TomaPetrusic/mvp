package com.meteoaktiv.forecast;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForecastReplaceService {

    private final ForecastHourlyRepository forecastHourlyRepository;

    public ForecastReplaceService(ForecastHourlyRepository forecastHourlyRepository) {
        this.forecastHourlyRepository = forecastHourlyRepository;
    }

    @Transactional
    public void replace(Long cityId, List<CombinedHour> combined) {
        Instant now = Instant.now();
        forecastHourlyRepository.deleteByCityId(cityId);
        for (CombinedHour hour : combined) {
            forecastHourlyRepository.save(new ForecastHourly(
                    cityId,
                    hour.time(),
                    hour.temperature(),
                    hour.precipitation(),
                    hour.windSpeed(),
                    hour.cloudCover(),
                    now));
        }
    }
}
