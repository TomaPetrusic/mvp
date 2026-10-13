package com.meteoaktiv.suitability;

import java.time.LocalDateTime;

import com.meteoaktiv.activity.Parameter;

public record HourData(
        LocalDateTime time,
        Double temperature,
        Double precipitation,
        Double windSpeed,
        Double cloudCover
) {
    public Double value(Parameter parameter) {
        return switch (parameter) {
            case TEMPERATURE -> temperature;
            case PRECIPITATION -> precipitation;
            case WIND_SPEED -> windSpeed;
            case CLOUD_COVER -> cloudCover;
        };
    }
}
