package com.meteoaktiv.suitability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meteoaktiv.activity.Parameter;

/**
 * Pure domain service: given an activity definition and a series of hourly forecast data,
 * decides for each of a set of days whether the activity is suitable, finds the best
 * (longest) qualifying run of consecutive suitable hours, and explains why a day fails.
 *
 * Holds no state and has no external dependencies, so it can be unit tested directly.
 */
public class SuitabilityService {

    public ActivityResult evaluate(ActivityDefinition activity, List<HourData> hours, List<LocalDate> days,
                                    LocalDateTime now) {
        Map<LocalDateTime, HourData> byTime = new HashMap<>();
        for (HourData hour : hours) {
            byTime.put(hour.time(), hour);
        }

        List<DaySuitability> dayResults = new ArrayList<>();
        for (LocalDate day : days) {
            dayResults.add(evaluateDay(activity, byTime, day));
        }

        boolean nowSuitable = evaluateNow(activity, byTime, now);

        return new ActivityResult(activity.activityId(), activity.name(), dayResults, nowSuitable);
    }

    private DaySuitability evaluateDay(ActivityDefinition activity, Map<LocalDateTime, HourData> byTime,
                                        LocalDate day) {
        List<LocalDateTime> windowTimestamps = windowTimestamps(activity, day);

        List<HourEvaluation> evaluations = new ArrayList<>();
        Map<Parameter, FailureStats> failureStats = new EnumMap<>(Parameter.class);

        for (LocalDateTime ts : windowTimestamps) {
            HourData data = byTime.get(ts);
            boolean hasData = data != null;
            boolean suitable = hasData && isHourSuitable(activity, data, failureStats);
            if (!hasData) {
                recordMissing(activity, failureStats);
            }
            evaluations.add(new HourEvaluation(
                    ts,
                    hasData ? data.temperature() : null,
                    hasData ? data.precipitation() : null,
                    hasData ? data.windSpeed() : null,
                    hasData ? data.cloudCover() : null,
                    hasData,
                    suitable));
        }

        TimeBlock bestBlock = findBestRun(evaluations, activity.minConsecutiveHours());
        boolean suitable = bestBlock != null;

        List<Reason> reasons = suitable ? List.of() : buildReasons(failureStats);

        return new DaySuitability(day, suitable, bestBlock, reasons, evaluations);
    }

    private boolean evaluateNow(ActivityDefinition activity, Map<LocalDateTime, HourData> byTime,
                                 LocalDateTime now) {
        LocalDateTime currentHour = now.withMinute(0).withSecond(0).withNano(0);
        if (!isInWindow(activity, currentHour)) {
            return false;
        }
        HourData data = byTime.get(currentHour);
        if (data == null) {
            return false;
        }
        return isHourSuitable(activity, data, null);
    }

    private boolean isInWindow(ActivityDefinition activity, LocalDateTime ts) {
        int hour = ts.getHour();
        if (!activity.crossesMidnight()) {
            return hour >= activity.windowStart() && hour < activity.windowEnd();
        }
        return hour >= activity.windowStart() || hour < activity.windowEnd();
    }

    /**
     * Builds the ordered sequence of hourly timestamps belonging to {@code day}'s occurrence of
     * the activity's window. For a window that crosses midnight, the night belongs to the date
     * on which it starts, so the tail hours (after midnight) are timestamped on {@code day + 1}.
     */
    private List<LocalDateTime> windowTimestamps(ActivityDefinition activity, LocalDate day) {
        List<LocalDateTime> result = new ArrayList<>();
        if (!activity.crossesMidnight()) {
            for (int h = activity.windowStart(); h < activity.windowEnd(); h++) {
                result.add(LocalDateTime.of(day, LocalTime.of(h, 0)));
            }
        } else {
            for (int h = activity.windowStart(); h < 24; h++) {
                result.add(LocalDateTime.of(day, LocalTime.of(h, 0)));
            }
            LocalDate nextDay = day.plusDays(1);
            for (int h = 0; h < activity.windowEnd(); h++) {
                result.add(LocalDateTime.of(nextDay, LocalTime.of(h, 0)));
            }
        }
        return result;
    }

    private boolean isHourSuitable(ActivityDefinition activity, HourData data,
                                    Map<Parameter, FailureStats> failureStats) {
        boolean allOk = true;
        for (ThresholdDef threshold : activity.thresholds()) {
            Double value = data.value(threshold.parameter());
            if (value == null) {
                allOk = false;
                if (failureStats != null) {
                    failureStats.computeIfAbsent(threshold.parameter(), p -> new FailureStats(threshold))
                            .missingCount++;
                }
                continue;
            }
            boolean ok = (threshold.minValue() == null || value >= threshold.minValue())
                    && (threshold.maxValue() == null || value <= threshold.maxValue());
            if (!ok) {
                allOk = false;
                if (failureStats != null) {
                    FailureStats stats = failureStats.computeIfAbsent(threshold.parameter(),
                            p -> new FailureStats(threshold));
                    stats.breachCount++;
                    if (threshold.maxValue() != null && value > threshold.maxValue()) {
                        if (stats.worstHigh == null || value > stats.worstHigh) {
                            stats.worstHigh = value;
                        }
                    }
                    if (threshold.minValue() != null && value < threshold.minValue()) {
                        if (stats.worstLow == null || value < stats.worstLow) {
                            stats.worstLow = value;
                        }
                    }
                }
            }
        }
        return allOk;
    }

    private void recordMissing(ActivityDefinition activity, Map<Parameter, FailureStats> failureStats) {
        for (ThresholdDef threshold : activity.thresholds()) {
            failureStats.computeIfAbsent(threshold.parameter(), p -> new FailureStats(threshold)).missingCount++;
        }
    }

    /** Finds the longest run of consecutive suitable hours that is at least minLength long. */
    private TimeBlock findBestRun(List<HourEvaluation> evaluations, int minLength) {
        int bestLength = 0;
        int bestStart = -1;

        int runStart = -1;
        for (int i = 0; i < evaluations.size(); i++) {
            if (evaluations.get(i).suitable()) {
                if (runStart == -1) {
                    runStart = i;
                }
                int length = i - runStart + 1;
                if (length > bestLength) {
                    bestLength = length;
                    bestStart = runStart;
                }
            } else {
                runStart = -1;
            }
        }

        if (bestLength < minLength) {
            return null;
        }
        LocalDateTime start = evaluations.get(bestStart).time();
        LocalDateTime end = evaluations.get(bestStart + bestLength - 1).time().plusHours(1);
        return new TimeBlock(start, end);
    }

    private List<Reason> buildReasons(Map<Parameter, FailureStats> failureStats) {
        return failureStats.values().stream()
                .filter(s -> s.totalCount() > 0)
                .sorted(Comparator.comparingInt(FailureStats::totalCount).reversed())
                .map(this::toReason)
                .toList();
    }

    private Reason toReason(FailureStats stats) {
        Parameter p = stats.threshold.parameter();
        if (stats.breachCount == 0 && stats.missingCount > 0) {
            return new Reason(p, p.label() + ": nedostaju podaci za dio sata");
        }
        String unit = p.unit();
        if (stats.worstHigh != null) {
            return new Reason(p, p.label() + " do " + format(stats.worstHigh) + " " + unit
                    + ", max " + format(stats.threshold.maxValue()));
        }
        if (stats.worstLow != null) {
            return new Reason(p, p.label() + " od " + format(stats.worstLow) + " " + unit
                    + ", min " + format(stats.threshold.minValue()));
        }
        return new Reason(p, p.label() + ": nedostaju podaci");
    }

    private String format(double value) {
        if (value == Math.rint(value)) {
            return String.format("%.0f", value);
        }
        return String.format("%.1f", value);
    }

    private static final class FailureStats {
        final ThresholdDef threshold;
        int breachCount = 0;
        int missingCount = 0;
        Double worstHigh;
        Double worstLow;

        FailureStats(ThresholdDef threshold) {
            this.threshold = threshold;
        }

        int totalCount() {
            return breachCount + missingCount;
        }
    }
}
