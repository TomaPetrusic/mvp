package com.meteoaktiv.suitability;

import java.time.LocalDateTime;

/** A contiguous run of suitable hours. {@code end} is exclusive (one hour past the last suitable hour). */
public record TimeBlock(LocalDateTime start, LocalDateTime end) {
}
