import { useState } from 'react';
import type { ActivityResult } from '../types';
import HourlyTable from './HourlyTable';

interface Props {
  results: ActivityResult[];
}

const DAY_FORMATTER = new Intl.DateTimeFormat('hr-HR', { weekday: 'short', day: 'numeric', month: 'numeric' });

function fmtDay(dateStr: string): string {
  return DAY_FORMATTER.format(new Date(dateStr + 'T00:00:00'));
}

function fmtBlock(start: string, end: string): string {
  return `${start.slice(11, 16)}–${end.slice(11, 16)}`;
}

export default function SuitabilityGrid({ results }: Props) {
  const [expanded, setExpanded] = useState<string | null>(null);

  if (results.length === 0) {
    return <p className="empty-hint">Odaberite bar jednu aktivnost.</p>;
  }

  const days = results[0].days;

  function cellKey(activityId: number, date: string): string {
    return `${activityId}:${date}`;
  }

  return (
    <div className="grid-wrap">
      <table className="suitability-grid">
        <thead>
          <tr>
            <th>Aktivnost</th>
            {days.map((d) => (
              <th key={d.date}>{fmtDay(d.date)}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {results.map((activity) => (
            <tr key={activity.activityId}>
              <th scope="row">{activity.activityName}</th>
              {activity.days.map((day) => {
                const key = cellKey(activity.activityId, day.date);
                const isExpanded = expanded === key;
                return (
                  <td key={day.date}>
                    <button
                      type="button"
                      className={`cell ${day.suitable ? 'cell-ok' : 'cell-no'}`}
                      onClick={() => setExpanded(isExpanded ? null : key)}
                    >
                      <span className="cell-icon">{day.suitable ? '✅' : '❌'}</span>
                      {day.suitable && day.bestBlock && (
                        <span className="cell-block">{fmtBlock(day.bestBlock.start, day.bestBlock.end)}</span>
                      )}
                      {!day.suitable && day.reasons.length > 0 && (
                        <span className="cell-reasons">
                          {day.reasons.slice(0, 2).map((r) => r.message).join('; ')}
                        </span>
                      )}
                    </button>
                    {isExpanded && (
                      <div className="cell-detail">
                        <HourlyTable day={day} />
                      </div>
                    )}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
