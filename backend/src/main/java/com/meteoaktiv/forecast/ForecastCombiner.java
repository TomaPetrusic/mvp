package com.meteoaktiv.forecast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.meteoaktiv.openmeteo.OpenMeteoHourly;
import com.meteoaktiv.openmeteo.OpenMeteoResponse;

/**
 * Combines the per-model hourly series of an Open-Meteo multi-model response into a single
 * series by averaging each parameter across whichever models returned a non-null value for
 * that hour. An hour with no models reporting a value for a parameter yields null.
 */
public class ForecastCombiner {

    public List<CombinedHour> combine(OpenMeteoResponse response) {
        List<CombinedHour> result = new ArrayList<>();
        if (response == null || response.hourly == null || response.hourly.time == null) {
            return result;
        }
        OpenMeteoHourly h = response.hourly;
        int n = h.time.size();
        for (int i = 0; i < n; i++) {
            LocalDateTime time = LocalDateTime.parse(h.time.get(i));
            Double temperature = average(
                    at(h.temperature_2m_ecmwf_ifs025, i),
                    at(h.temperature_2m_icon_seamless, i),
                    at(h.temperature_2m_gfs_seamless, i));
            Double precipitation = average(
                    at(h.precipitation_ecmwf_ifs025, i),
                    at(h.precipitation_icon_seamless, i),
                    at(h.precipitation_gfs_seamless, i));
            Double windSpeed = average(
                    at(h.wind_speed_10m_ecmwf_ifs025, i),
                    at(h.wind_speed_10m_icon_seamless, i),
                    at(h.wind_speed_10m_gfs_seamless, i));
            Double cloudCover = average(
                    at(h.cloud_cover_ecmwf_ifs025, i),
                    at(h.cloud_cover_icon_seamless, i),
                    at(h.cloud_cover_gfs_seamless, i));
            result.add(new CombinedHour(time, temperature, precipitation, windSpeed, cloudCover));
        }
        return result;
    }

    private Double at(List<Double> values, int index) {
        if (values == null || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    private Double average(Double... values) {
        double sum = 0;
        int count = 0;
        for (Double v : values) {
            if (v != null) {
                sum += v;
                count++;
            }
        }
        if (count == 0) {
            return null;
        }
        return sum / count;
    }
}
