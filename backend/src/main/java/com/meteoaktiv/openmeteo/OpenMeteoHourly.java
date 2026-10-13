package com.meteoaktiv.openmeteo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Raw hourly block of an Open-Meteo multi-model response. Each parameter comes back once per
 * requested model, with the model id appended as a suffix to the variable name
 * (e.g. temperature_2m_ecmwf_ifs025). A model may return null for a given hour.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoHourly {

    public List<String> time;

    public List<Double> temperature_2m_ecmwf_ifs025;
    public List<Double> precipitation_ecmwf_ifs025;
    public List<Double> wind_speed_10m_ecmwf_ifs025;
    public List<Double> cloud_cover_ecmwf_ifs025;

    public List<Double> temperature_2m_icon_seamless;
    public List<Double> precipitation_icon_seamless;
    public List<Double> wind_speed_10m_icon_seamless;
    public List<Double> cloud_cover_icon_seamless;

    public List<Double> temperature_2m_gfs_seamless;
    public List<Double> precipitation_gfs_seamless;
    public List<Double> wind_speed_10m_gfs_seamless;
    public List<Double> cloud_cover_gfs_seamless;
}
