package com.meteoaktiv.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "meteoaktiv.openmeteo")
public class OpenMeteoProperties {

    private String baseUrl = "https://api.open-meteo.com/v1/forecast";
    private String models = "ecmwf_ifs025,icon_seamless,gfs_seamless";
    private int forecastDays = 3;
    private String timezone = "Europe/Zagreb";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModels() {
        return models;
    }

    public void setModels(String models) {
        this.models = models;
    }

    public int getForecastDays() {
        return forecastDays;
    }

    public void setForecastDays(int forecastDays) {
        this.forecastDays = forecastDays;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
