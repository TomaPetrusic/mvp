package com.meteoaktiv.web;

import java.util.List;

import com.meteoaktiv.suitability.ActivityResult;
import com.meteoaktiv.suitability.SuitabilityFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suitability")
public class SuitabilityController {

    private final SuitabilityFacade suitabilityFacade;

    public SuitabilityController(SuitabilityFacade suitabilityFacade) {
        this.suitabilityFacade = suitabilityFacade;
    }

    @GetMapping
    public List<ActivityResult> suitability(@RequestParam Long cityId, @RequestParam String activityIds) {
        List<Long> ids = List.of(activityIds.split(",")).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toList();
        return suitabilityFacade.evaluate(cityId, ids);
    }
}
