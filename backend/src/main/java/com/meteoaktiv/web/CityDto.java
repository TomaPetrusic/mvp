package com.meteoaktiv.web;

import com.meteoaktiv.city.City;

public record CityDto(Long id, String name, double lat, double lon) {
    public static CityDto from(City city) {
        return new CityDto(city.getId(), city.getName(), city.getLat(), city.getLon());
    }
}
