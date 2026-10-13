package com.meteoaktiv.suitability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.meteoaktiv.activity.Parameter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuitabilityServiceTest {

    private final SuitabilityService service = new SuitabilityService();
    private final LocalDate day = LocalDate.of(2026, 10, 14);

    private HourData hour(LocalDate d, int hour, double temp, double precip, double wind, double cloud) {
        return new HourData(LocalDateTime.of(d, LocalTime.of(hour, 0)), temp, precip, wind, cloud);
    }

    @Test
    void normalDay_findsLongestQualifyingRun() {
        ActivityDefinition hiking = new ActivityDefinition(1L, "Planinarenje", 7, 19, 4, List.of(
                new ThresholdDef(Parameter.TEMPERATURE, 5.0, 30.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 0.5),
                new ThresholdDef(Parameter.WIND_SPEED, null, 30.0)
        ));

        List<HourData> hours = new ArrayList<>();
        // 07-08 unsuitable (too windy), 09-14 suitable (6h run), 15-18 unsuitable (rain)
        hours.add(hour(day, 7, 15, 0, 40, 10));
        hours.add(hour(day, 8, 15, 0, 40, 10));
        for (int h = 9; h <= 14; h++) {
            hours.add(hour(day, h, 18, 0, 10, 20));
        }
        for (int h = 15; h <= 18; h++) {
            hours.add(hour(day, h, 18, 2.0, 10, 20));
        }

        ActivityResult result = service.evaluate(hiking, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(12, 0)));

        DaySuitability dayResult = result.days().get(0);
        assertThat(dayResult.suitable()).isTrue();
        assertThat(dayResult.bestBlock().start()).isEqualTo(LocalDateTime.of(day, LocalTime.of(9, 0)));
        assertThat(dayResult.bestBlock().end()).isEqualTo(LocalDateTime.of(day, LocalTime.of(15, 0)));
    }

    @Test
    void midnightCrossingWindow_assignsNightToStartingDate() {
        ActivityDefinition astronomy = new ActivityDefinition(4L, "Astronomija", 21, 4, 3, List.of(
                new ThresholdDef(Parameter.CLOUD_COVER, null, 20.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 0.0),
                new ThresholdDef(Parameter.WIND_SPEED, null, 20.0)
        ));

        List<HourData> hours = new ArrayList<>();
        hours.add(hour(day, 21, 10, 0, 10, 80));       // cloudy, unsuitable
        hours.add(hour(day, 22, 10, 0, 10, 5));         // clear, suitable
        hours.add(hour(day, 23, 10, 0, 10, 5));         // clear, suitable
        hours.add(hour(day.plusDays(1), 0, 10, 0, 10, 5)); // clear, suitable (after midnight)
        hours.add(hour(day.plusDays(1), 1, 10, 0, 10, 90)); // cloudy, unsuitable
        hours.add(hour(day.plusDays(1), 2, 10, 0, 10, 90));
        hours.add(hour(day.plusDays(1), 3, 10, 0, 10, 90));

        ActivityResult result = service.evaluate(astronomy, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(12, 0)));

        DaySuitability dayResult = result.days().get(0);
        assertThat(dayResult.suitable()).isTrue();
        assertThat(dayResult.bestBlock().start()).isEqualTo(LocalDateTime.of(day, LocalTime.of(22, 0)));
        assertThat(dayResult.bestBlock().end()).isEqualTo(LocalDateTime.of(day.plusDays(1), LocalTime.of(1, 0)));
        // the whole night (21 -> 03:59 next day) is attributed to `day`, not day+1
        assertThat(dayResult.hourly()).hasSize(7);
    }

    @Test
    void runExactlyMinimumLength_isSuitable() {
        ActivityDefinition running = new ActivityDefinition(2L, "Trčanje", 6, 21, 2, List.of(
                new ThresholdDef(Parameter.TEMPERATURE, 0.0, 28.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 0.3),
                new ThresholdDef(Parameter.WIND_SPEED, null, 25.0)
        ));

        List<HourData> hours = new ArrayList<>();
        hours.add(hour(day, 6, 40, 0, 10, 10));  // too hot
        hours.add(hour(day, 7, 15, 0, 10, 10));  // suitable
        hours.add(hour(day, 8, 15, 0, 10, 10));  // suitable (run of exactly 2)
        hours.add(hour(day, 9, 40, 0, 10, 10));  // too hot

        ActivityResult result = service.evaluate(running, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(10, 0)));

        DaySuitability dayResult = result.days().get(0);
        assertThat(dayResult.suitable()).isTrue();
        assertThat(dayResult.bestBlock().start()).isEqualTo(LocalDateTime.of(day, LocalTime.of(7, 0)));
        assertThat(dayResult.bestBlock().end()).isEqualTo(LocalDateTime.of(day, LocalTime.of(9, 0)));
    }

    @Test
    void runShorterThanMinimum_isNotSuitableAndExplainsWhy() {
        ActivityDefinition fishing = new ActivityDefinition(3L, "Ribolov", 5, 21, 3, List.of(
                new ThresholdDef(Parameter.TEMPERATURE, 5.0, 30.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 1.0),
                new ThresholdDef(Parameter.WIND_SPEED, null, 20.0)
        ));

        List<HourData> hours = new ArrayList<>();
        hours.add(hour(day, 5, 15, 0, 10, 10));  // suitable
        hours.add(hour(day, 6, 15, 0, 10, 10));  // suitable (run of 2, breaks right after)
        hours.add(hour(day, 7, 15, 0, 42, 10));  // too windy, breaks the run
        hours.add(hour(day, 8, 15, 0, 10, 10));  // suitable
        hours.add(hour(day, 9, 15, 0, 10, 10));  // suitable (run of 2)

        ActivityResult result = service.evaluate(fishing, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(5, 0)));

        DaySuitability dayResult = result.days().get(0);
        assertThat(dayResult.suitable()).isFalse();
        assertThat(dayResult.bestBlock()).isNull();
        assertThat(dayResult.reasons()).isNotEmpty();
        Reason windReason = dayResult.reasons().get(0);
        assertThat(windReason.parameter()).isEqualTo(Parameter.WIND_SPEED);
        assertThat(windReason.message()).contains("42").contains("max 20");
    }

    @Test
    void missingHourData_isTreatedAsUnsuitableWithoutCrashing() {
        ActivityDefinition fishing = new ActivityDefinition(3L, "Ribolov", 5, 9, 3, List.of(
                new ThresholdDef(Parameter.TEMPERATURE, 5.0, 30.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 1.0),
                new ThresholdDef(Parameter.WIND_SPEED, null, 20.0)
        ));

        // hour 7 is entirely missing from the fetched data (e.g. provider gap)
        List<HourData> hours = new ArrayList<>();
        hours.add(hour(day, 5, 15, 0, 10, 10));
        hours.add(hour(day, 6, 15, 0, 10, 10));
        hours.add(hour(day, 8, 15, 0, 10, 10));

        ActivityResult result = service.evaluate(fishing, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(5, 0)));

        DaySuitability dayResult = result.days().get(0);
        assertThat(dayResult.suitable()).isFalse(); // longest run is 2 (5-6), needs 3
        assertThat(dayResult.hourly()).hasSize(4); // window 5-8 inclusive, end exclusive at 9
        HourEvaluation missingHour = dayResult.hourly().stream()
                .filter(h -> h.time().getHour() == 7)
                .findFirst().orElseThrow();
        assertThat(missingHour.hasData()).isFalse();
        assertThat(missingHour.suitable()).isFalse();
        assertThat(dayResult.reasons()).isNotEmpty();
    }

    @Test
    void nullThreshold_meansNoLowerOrUpperBound() {
        // precipitation has only a max bound (null min) - a very low temperature with no
        // lower temperature bound at all must still count as suitable.
        ActivityDefinition noLowerTempBound = new ActivityDefinition(5L, "Test", 0, 6, 1, List.of(
                new ThresholdDef(Parameter.TEMPERATURE, null, 30.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 0.5)
        ));

        List<HourData> hours = new ArrayList<>();
        hours.add(hour(day, 0, -15.0, 0, 5, 10)); // extremely cold, but no min bound set

        ActivityResult result = service.evaluate(noLowerTempBound, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(0, 0)));

        DaySuitability dayResult = result.days().get(0);
        assertThat(dayResult.hourly().get(0).suitable()).isTrue();
    }

    @Test
    void nowSuitability_reflectsCurrentHourWithinWindow() {
        ActivityDefinition running = new ActivityDefinition(2L, "Trčanje", 6, 21, 2, List.of(
                new ThresholdDef(Parameter.TEMPERATURE, 0.0, 28.0),
                new ThresholdDef(Parameter.PRECIPITATION, null, 0.3),
                new ThresholdDef(Parameter.WIND_SPEED, null, 25.0)
        ));

        List<HourData> hours = List.of(hour(day, 10, 18, 0, 10, 10));

        ActivityResult result = service.evaluate(running, hours, List.of(day),
                LocalDateTime.of(day, LocalTime.of(10, 30)));

        assertThat(result.now()).isTrue();
    }
}
