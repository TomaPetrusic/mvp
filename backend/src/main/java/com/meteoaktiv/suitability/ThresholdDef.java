package com.meteoaktiv.suitability;

import com.meteoaktiv.activity.Parameter;

public record ThresholdDef(Parameter parameter, Double minValue, Double maxValue) {
}
