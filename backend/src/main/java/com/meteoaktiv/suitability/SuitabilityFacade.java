package com.meteoaktiv.suitability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.meteoaktiv.activity.Activity;
import com.meteoaktiv.activity.ActivityRepository;
import com.meteoaktiv.activity.Threshold;
import com.meteoaktiv.activity.ThresholdRepository;
import com.meteoaktiv.city.City;
import com.meteoaktiv.city.CityRepository;
import com.meteoaktiv.forecast.ForecastHourly;
import com.meteoaktiv.forecast.ForecastHourlyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SuitabilityFacade {

    private static final ZoneId ZAGREB = ZoneId.of("Europe/Zagreb");

    private final CityRepository cityRepository;
    private final ActivityRepository activityRepository;
    private final ThresholdRepository thresholdRepository;
    private final ForecastHourlyRepository forecastHourlyRepository;
    private final SuitabilityService suitabilityService = new SuitabilityService();

    public SuitabilityFacade(CityRepository cityRepository, ActivityRepository activityRepository,
                              ThresholdRepository thresholdRepository,
                              ForecastHourlyRepository forecastHourlyRepository) {
        this.cityRepository = cityRepository;
        this.activityRepository = activityRepository;
        this.thresholdRepository = thresholdRepository;
        this.forecastHourlyRepository = forecastHourlyRepository;
    }

    public List<ActivityResult> evaluate(Long cityId, List<Long> activityIds) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grad nije pronađen"));

        LocalDateTime now = ZonedDateTime.now(ZAGREB).toLocalDateTime().withMinute(0).withSecond(0).withNano(0);
        LocalDate today = now.toLocalDate();
        List<LocalDate> days = List.of(today, today.plusDays(1), today.plusDays(2));

        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(3).atStartOfDay();
        List<ForecastHourly> stored = forecastHourlyRepository
                .findByCityIdAndTimeBetweenOrderByTime(cityId, from, to);
        List<HourData> hourData = stored.stream()
                .map(f -> new HourData(f.getTime(), f.getTemperature(), f.getPrecipitation(),
                        f.getWindSpeed(), f.getCloudCover()))
                .toList();

        return activityIds.stream()
                .map(id -> activityRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Aktivnost " + id + " nije pronađena")))
                .map(activity -> suitabilityService.evaluate(toDefinition(activity), hourData, days, now))
                .toList();
    }

    private ActivityDefinition toDefinition(Activity activity) {
        List<Threshold> thresholds = thresholdRepository.findByActivityId(activity.getId());
        List<ThresholdDef> defs = thresholds.stream()
                .map(t -> new ThresholdDef(t.getParameter(), t.getMinValue(), t.getMaxValue()))
                .toList();
        return new ActivityDefinition(activity.getId(), activity.getName(), activity.getWindowStart(),
                activity.getWindowEnd(), activity.getMinConsecutiveHours(), defs);
    }
}
