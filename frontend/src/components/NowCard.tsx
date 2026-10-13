import type { ActivityResult } from '../types';

interface Props {
  results: ActivityResult[];
}

export default function NowCard({ results }: Props) {
  if (results.length === 0) {
    return null;
  }
  return (
    <section className="now-card">
      <h2>Sada</h2>
      <div className="now-row">
        {results.map((r) => (
          <div key={r.activityId} className={`now-pill ${r.now ? 'ok' : 'no'}`}>
            <span className="now-icon">{r.now ? '✅' : '❌'}</span>
            <span>{r.activityName}</span>
          </div>
        ))}
      </div>
    </section>
  );
}
