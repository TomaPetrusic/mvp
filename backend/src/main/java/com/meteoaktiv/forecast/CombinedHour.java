package com.meteoaktiv.forecast;

import java.time.LocalDateTime;

public record CombinedHour(
        LocalDateTime time,
        Double temperature,
        Double precipitation,
        Double windSpeed,
        Double cloudCover
) {
}
