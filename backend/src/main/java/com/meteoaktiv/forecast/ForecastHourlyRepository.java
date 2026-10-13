package com.meteoaktiv.forecast;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ForecastHourlyRepository extends JpaRepository<ForecastHourly, Long> {

    List<ForecastHourly> findByCityIdAndTimeBetweenOrderByTime(Long cityId, LocalDateTime from, LocalDateTime to);

    List<ForecastHourly> findByTime(LocalDateTime time);

    @Modifying
    @Query("delete from ForecastHourly f where f.cityId = :cityId")
    void deleteByCityId(@Param("cityId") Long cityId);

    @Query("select max(f.fetchedAt) from ForecastHourly f")
    Optional<Instant> findMaxFetchedAt();
}
