package com.meteoaktiv.web;

import com.meteoaktiv.activity.Activity;

public record ActivityDto(Long id, String name, int windowStart, int windowEnd, int minConsecutiveHours) {
    public static ActivityDto from(Activity activity) {
        return new ActivityDto(activity.getId(), activity.getName(), activity.getWindowStart(),
                activity.getWindowEnd(), activity.getMinConsecutiveHours());
    }
}
