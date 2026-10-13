package com.meteoaktiv.suitability;

import java.time.LocalDate;
import java.util.List;

public record DaySuitability(
        LocalDate date,
        boolean suitable,
        TimeBlock bestBlock,
        List<Reason> reasons,
        List<HourEvaluation> hourly
) {
}
