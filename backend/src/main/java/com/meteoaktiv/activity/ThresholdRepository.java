package com.meteoaktiv.activity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThresholdRepository extends JpaRepository<Threshold, Long> {

    List<Threshold> findByActivityId(Long activityId);
}
