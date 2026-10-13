import type { City } from '../types';

interface Props {
  cities: City[];
  selectedCityId: number | null;
  onChange: (cityId: number) => void;
}

export default function CitySelect({ cities, selectedCityId, onChange }: Props) {
  return (
    <label className="field">
      <span className="field-label">Grad</span>
      <select
        value={selectedCityId ?? ''}
        onChange={(e) => onChange(Number(e.target.value))}
      >
        {cities.map((city) => (
          <option key={city.id} value={city.id}>
            {city.name}
          </option>
        ))}
      </select>
    </label>
  );
}
