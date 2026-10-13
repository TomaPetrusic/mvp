package com.meteoaktiv.web;

import java.util.List;

import com.meteoaktiv.city.CityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    public List<CityDto> all() {
        return cityRepository.findAll().stream().map(CityDto::from).toList();
    }
}
