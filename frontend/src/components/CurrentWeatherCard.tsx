import type { CurrentConditions } from '../types';
import { summarize, tempColor } from '../weather';

interface Props {
  current: CurrentConditions | null;
  cityName: string;
}

function fmt(v: number | null, digits = 0): string {
  return v === null ? '—' : v.toFixed(digits);
}

export default function CurrentWeatherCard({ current, cityName }: Props) {
  if (!current || !current.hasData) {
    return (
      <section className="weather-card">
        <h2>Trenutno u {cityName}</h2>
        <p className="empty-hint">Podaci trenutno nisu dostupni.</p>
      </section>
    );
  }

  const { icon, label } = summarize(current.cloudCover, current.precipitation);

  return (
    <section className="weather-card">
      <h2>Trenutno u {cityName}</h2>
      <div className="weather-main">
        <span className="weather-icon" aria-hidden="true">{icon}</span>
        <span className="weather-temp" style={{ color: tempColor(current.temperature) }}>
          {fmt(current.temperature, 1)}°
        </span>
        <span className="weather-label">{label}</span>
      </div>
      <div className="weather-stats">
        <div className="weather-stat">
          <span className="weather-stat-label">Naoblaka</span>
          <div className="meter">
            <div className="meter-fill" style={{ width: `${Math.min(100, current.cloudCover ?? 0)}%` }} />
          </div>
          <span className="weather-stat-value">{fmt(current.cloudCover)}%</span>
        </div>
        <div className="weather-stat">
          <span className="weather-stat-label">Vjetar</span>
          <span className="weather-stat-value">{fmt(current.windSpeed)} km/h</span>
        </div>
        <div className="weather-stat">
          <span className="weather-stat-label">Oborine</span>
          <span className="weather-stat-value">{fmt(current.precipitation, 1)} mm</span>
        </div>
      </div>
    </section>
  );
}
