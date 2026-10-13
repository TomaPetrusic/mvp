import { useMemo } from 'react';
import { MapContainer, TileLayer, Marker, Tooltip } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import type { City, CurrentConditions } from '../types';
import { tempColor } from '../weather';

interface Props {
  cities: City[];
  current: CurrentConditions[];
  selectedCityId: number | null;
  onSelect: (cityId: number) => void;
}

const CROATIA_CENTER: [number, number] = [45.3, 16.7];

function markerIcon(temp: number | null, selected: boolean): L.DivIcon {
  const color = tempColor(temp);
  const label = temp === null ? '—' : `${Math.round(temp)}°`;
  const size = selected ? 42 : 34;
  return L.divIcon({
    className: '',
    html: `<div class="map-marker${selected ? ' map-marker-selected' : ''}" style="background:${color}">${label}</div>`,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
  });
}

export default function CityMap({ cities, current, selectedCityId, onSelect }: Props) {
  const byCity = useMemo(() => {
    const map = new Map<number, CurrentConditions>();
    for (const c of current) map.set(c.cityId, c);
    return map;
  }, [current]);

  return (
    <section className="map-card">
      <h2>Karta gradova</h2>
      <div className="map-wrap">
        <MapContainer
          center={CROATIA_CENTER}
          zoom={7}
          scrollWheelZoom={false}
          style={{ height: '100%', width: '100%' }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {cities.map((city) => {
            const cond = byCity.get(city.id) ?? null;
            const selected = city.id === selectedCityId;
            return (
              <Marker
                key={city.id}
                position={[city.lat, city.lon]}
                icon={markerIcon(cond?.temperature ?? null, selected)}
                eventHandlers={{ click: () => onSelect(city.id) }}
              >
                <Tooltip direction="top" offset={[0, -18]}>
                  {city.name}
                  {cond?.hasData ? ` · ${cond.temperature?.toFixed(1)}°C` : ''}
                </Tooltip>
              </Marker>
            );
          })}
        </MapContainer>
      </div>
    </section>
  );
}
