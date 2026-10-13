package com.meteoaktiv.activity;

public enum Parameter {
    TEMPERATURE("Temperatura", "°C"),
    PRECIPITATION("Oborine", "mm"),
    WIND_SPEED("Vjetar", "km/h"),
    CLOUD_COVER("Naoblaka", "%");

    private final String label;
    private final String unit;

    Parameter(String label, String unit) {
        this.label = label;
        this.unit = unit;
    }

    public String label() {
        return label;
    }

    public String unit() {
        return unit;
    }
}
