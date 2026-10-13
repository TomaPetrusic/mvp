import type { Activity } from '../types';

interface Props {
  activities: Activity[];
  selectedIds: number[];
  onToggle: (activityId: number) => void;
}

export default function ActivitySelect({ activities, selectedIds, onToggle }: Props) {
  return (
    <fieldset className="field activity-select">
      <span className="field-label">Aktivnosti</span>
      <div className="chip-row">
        {activities.map((activity) => {
          const active = selectedIds.includes(activity.id);
          return (
            <button
              key={activity.id}
              type="button"
              className={`chip ${active ? 'chip-active' : ''}`}
              aria-pressed={active}
              onClick={() => onToggle(activity.id)}
            >
              {activity.name}
            </button>
          );
        })}
      </div>
    </fieldset>
  );
}
