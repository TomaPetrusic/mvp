-- window_start / window_end are local hours (0-23). window_end may be <= window_start,
-- which means the window crosses midnight (e.g. 21 -> 4).
INSERT INTO activities (name, window_start, window_end, min_consecutive_hours) VALUES
    ('Planinarenje', 7, 19, 4),
    ('Trčanje', 6, 21, 2),
    ('Ribolov', 5, 21, 3),
    ('Astronomija', 21, 4, 3);

-- Planinarenje: temp 5-30 C, precipitation <= 0.5 mm, wind <= 30 km/h
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'TEMPERATURE', 5, 30 FROM activities WHERE name = 'Planinarenje';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'PRECIPITATION', NULL, 0.5 FROM activities WHERE name = 'Planinarenje';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'WIND_SPEED', NULL, 30 FROM activities WHERE name = 'Planinarenje';

-- Trčanje: temp 0-28 C, precipitation <= 0.3 mm, wind <= 25 km/h
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'TEMPERATURE', 0, 28 FROM activities WHERE name = 'Trčanje';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'PRECIPITATION', NULL, 0.3 FROM activities WHERE name = 'Trčanje';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'WIND_SPEED', NULL, 25 FROM activities WHERE name = 'Trčanje';

-- Ribolov: temp 5-30 C, precipitation <= 1.0 mm, wind <= 20 km/h
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'TEMPERATURE', 5, 30 FROM activities WHERE name = 'Ribolov';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'PRECIPITATION', NULL, 1.0 FROM activities WHERE name = 'Ribolov';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'WIND_SPEED', NULL, 20 FROM activities WHERE name = 'Ribolov';

-- Astronomija: cloud cover <= 20%, precipitation = 0 mm, wind <= 20 km/h
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'CLOUD_COVER', NULL, 20 FROM activities WHERE name = 'Astronomija';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'PRECIPITATION', NULL, 0 FROM activities WHERE name = 'Astronomija';
INSERT INTO thresholds (activity_id, parameter, min_value, max_value)
SELECT id, 'WIND_SPEED', NULL, 20 FROM activities WHERE name = 'Astronomija';
