package com.meteoaktiv.suitability;

import java.time.LocalDateTime;

public record HourEvaluation(
        LocalDateTime time,
        Double temperature,
        Double precipitation,
        Double windSpeed,
        Double cloudCover,
        boolean hasData,
        boolean suitable
) {
}
