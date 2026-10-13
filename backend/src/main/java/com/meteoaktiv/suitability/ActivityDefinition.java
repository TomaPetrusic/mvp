package com.meteoaktiv.suitability;

import java.util.List;

public record ActivityDefinition(
        Long activityId,
        String name,
        int windowStart,
        int windowEnd,
        int minConsecutiveHours,
        List<ThresholdDef> thresholds
) {
    public boolean crossesMidnight() {
        return windowEnd <= windowStart;
    }
}
