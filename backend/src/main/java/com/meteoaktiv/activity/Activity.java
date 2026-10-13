package com.meteoaktiv.activity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /** Local hour 0-23 the activity window starts at (inclusive). */
    private int windowStart;

    /**
     * Local hour 0-23 the activity window ends at (exclusive). If windowEnd &lt;= windowStart,
     * the window crosses midnight (e.g. 21 -&gt; 4 means 21:00-03:59 the next calendar day).
     */
    private int windowEnd;

    private int minConsecutiveHours;

    protected Activity() {
    }

    public Activity(String name, int windowStart, int windowEnd, int minConsecutiveHours) {
        this.name = name;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.minConsecutiveHours = minConsecutiveHours;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWindowStart() {
        return windowStart;
    }

    public int getWindowEnd() {
        return windowEnd;
    }

    public int getMinConsecutiveHours() {
        return minConsecutiveHours;
    }

    public boolean crossesMidnight() {
        return windowEnd <= windowStart;
    }
}
