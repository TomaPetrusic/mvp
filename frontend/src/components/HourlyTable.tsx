import type { DaySuitability } from '../types';

function fmtHour(iso: string): string {
  return iso.slice(11, 16);
}

function fmtNum(v: number | null, digits = 1): string {
  return v === null ? '—' : v.toFixed(digits);
}

export default function HourlyTable({ day }: { day: DaySuitability }) {
  return (
    <table className="hourly-table">
      <thead>
        <tr>
          <th>Sat</th>
          <th>Temp (°C)</th>
          <th>Oborine (mm)</th>
          <th>Vjetar (km/h)</th>
          <th>Naoblaka (%)</th>
          <th>Pogodno</th>
        </tr>
      </thead>
      <tbody>
        {day.hourly.map((h) => (
          <tr key={h.time} className={h.suitable ? 'row-ok' : 'row-no'}>
            <td>{fmtHour(h.time)}</td>
            <td>{fmtNum(h.temperature)}</td>
            <td>{fmtNum(h.precipitation, 2)}</td>
            <td>{fmtNum(h.windSpeed, 0)}</td>
            <td>{fmtNum(h.cloudCover, 0)}</td>
            <td>{h.hasData ? (h.suitable ? '✅' : '❌') : '?'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
