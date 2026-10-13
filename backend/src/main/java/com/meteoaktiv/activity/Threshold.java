package com.meteoaktiv.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "thresholds")
public class Threshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long activityId;

    @Enumerated(EnumType.STRING)
    private Parameter parameter;

    private Double minValue;

    private Double maxValue;

    protected Threshold() {
    }

    public Threshold(Long activityId, Parameter parameter, Double minValue, Double maxValue) {
        this.activityId = activityId;
        this.parameter = parameter;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Long getId() {
        return id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }
}
