package com.meteoaktiv.suitability;

import com.meteoaktiv.activity.Parameter;

public record Reason(Parameter parameter, String message) {
}
