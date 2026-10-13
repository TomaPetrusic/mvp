package com.meteoaktiv.suitability;

import java.util.List;

public record ActivityResult(
        Long activityId,
        String activityName,
        List<DaySuitability> days,
        boolean now
) {
}
