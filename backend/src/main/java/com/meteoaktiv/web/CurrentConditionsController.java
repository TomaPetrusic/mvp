package com.meteoaktiv.web;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meteoaktiv.city.City;
import com.meteoaktiv.city.CityRepository;
import com.meteoaktiv.forecast.ForecastHourly;
import com.meteoaktiv.forecast.ForecastHourlyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/current")
public class CurrentConditionsController {

    private static final ZoneId ZAGREB = ZoneId.of("Europe/Zagreb");

    private final CityRepository cityRepository;
    private final ForecastHourlyRepository forecastHourlyRepository;

    public CurrentConditionsController(CityRepository cityRepository,
                                        ForecastHourlyRepository forecastHourlyRepository) {
        this.cityRepository = cityRepository;
        this.forecastHourlyRepository = forecastHourlyRepository;
    }

    @GetMapping
    public List<CurrentConditionsDto> current() {
        LocalDateTime currentHour = ZonedDateTime.now(ZAGREB).toLocalDateTime()
                .withMinute(0).withSecond(0).withNano(0);

        Map<Long, ForecastHourly> byCity = new HashMap<>();
        for (ForecastHourly f : forecastHourlyRepository.findByTime(currentHour)) {
            byCity.put(f.getCityId(), f);
        }

        List<City> cities = cityRepository.findAll();
        return cities.stream()
                .map(city -> toDto(city, currentHour, byCity.get(city.getId())))
                .toList();
    }

    private CurrentConditionsDto toDto(City city, LocalDateTime time, ForecastHourly f) {
        if (f == null) {
            return new CurrentConditionsDto(city.getId(), city.getName(), city.getLat(), city.getLon(),
                    time, null, null, null, null, false);
        }
        return new CurrentConditionsDto(city.getId(), city.getName(), city.getLat(), city.getLon(),
                time, f.getTemperature(), f.getPrecipitation(), f.getWindSpeed(), f.getCloudCover(), true);
    }
}
