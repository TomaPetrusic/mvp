package com.meteoaktiv.web;

import java.util.List;

import com.meteoaktiv.activity.ActivityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping
    public List<ActivityDto> all() {
        return activityRepository.findAll().stream().map(ActivityDto::from).toList();
    }
}
