package com.meteoaktiv.forecast;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "forecast_hourly")
public class ForecastHourly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cityId;

    private LocalDateTime time;

    private Double temperature;

    private Double precipitation;

    private Double windSpeed;

    private Double cloudCover;

    private Instant fetchedAt;

    protected ForecastHourly() {
    }

    public ForecastHourly(Long cityId, LocalDateTime time, Double temperature, Double precipitation,
                           Double windSpeed, Double cloudCover, Instant fetchedAt) {
        this.cityId = cityId;
        this.time = time;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
        this.cloudCover = cloudCover;
        this.fetchedAt = fetchedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCityId() {
        return cityId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getPrecipitation() {
        return precipitation;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Double getCloudCover() {
        return cloudCover;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }
}
