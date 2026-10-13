CREATE TABLE cities (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lat  DOUBLE PRECISION NOT NULL,
    lon  DOUBLE PRECISION NOT NULL
);

CREATE TABLE activities (
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(100) NOT NULL,
    window_start           INTEGER NOT NULL,
    window_end             INTEGER NOT NULL,
    min_consecutive_hours  INTEGER NOT NULL
);

CREATE TABLE thresholds (
    id          BIGSERIAL PRIMARY KEY,
    activity_id BIGINT NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    parameter   VARCHAR(30) NOT NULL,
    min_value   DOUBLE PRECISION,
    max_value   DOUBLE PRECISION
);

CREATE TABLE forecast_hourly (
    id           BIGSERIAL PRIMARY KEY,
    city_id      BIGINT NOT NULL REFERENCES cities(id) ON DELETE CASCADE,
    time         TIMESTAMP NOT NULL,
    temperature  DOUBLE PRECISION,
    precipitation DOUBLE PRECISION,
    wind_speed   DOUBLE PRECISION,
    cloud_cover  DOUBLE PRECISION,
    fetched_at   TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_forecast_hourly_city_time ON forecast_hourly (city_id, time);
CREATE INDEX idx_thresholds_activity ON thresholds (activity_id);
