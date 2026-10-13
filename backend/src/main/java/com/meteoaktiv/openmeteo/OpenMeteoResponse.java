package com.meteoaktiv.openmeteo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponse {

    public double latitude;
    public double longitude;
    public OpenMeteoHourly hourly;
}
