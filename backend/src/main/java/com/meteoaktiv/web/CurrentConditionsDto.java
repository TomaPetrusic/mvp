package com.meteoaktiv.web;

import java.time.LocalDateTime;

public record CurrentConditionsDto(
        Long cityId,
        String cityName,
        double lat,
        double lon,
        LocalDateTime time,
        Double temperature,
        Double precipitation,
        Double windSpeed,
        Double cloudCover,
        boolean hasData) {
}
