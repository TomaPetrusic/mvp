import { useCallback, useEffect, useMemo, useState } from 'react';
import { fetchActivities, fetchCities, fetchCurrent, fetchStatus, fetchSuitability } from './api';
import type { Activity, ActivityResult, City, CurrentConditions } from './types';
import CitySelect from './components/CitySelect';
import ActivitySelect from './components/ActivitySelect';
import NowCard from './components/NowCard';
import CurrentWeatherCard from './components/CurrentWeatherCard';
import CityMap from './components/CityMap';
import SuitabilityGrid from './components/SuitabilityGrid';
import StatusFooter from './components/StatusFooter';

const CURRENT_REFRESH_MS = 5 * 60 * 1000;

function parseParams(): { city: number | null; activities: number[] } {
  const params = new URLSearchParams(window.location.search);
  const cityParam = params.get('city');
  const activitiesParam = params.get('a');
  return {
    city: cityParam ? Number(cityParam) : null,
    activities: activitiesParam
      ? activitiesParam.split(',').map(Number).filter((n) => !Number.isNaN(n))
      : [],
  };
}

function writeParams(cityId: number | null, activityIds: number[]) {
  const params = new URLSearchParams();
  if (cityId !== null) {
    params.set('city', String(cityId));
  }
  if (activityIds.length > 0) {
    params.set('a', activityIds.join(','));
  }
  const query = params.toString();
  const newUrl = `${window.location.pathname}${query ? `?${query}` : ''}`;
  window.history.replaceState(null, '', newUrl);
}

export default function App() {
  const [cities, setCities] = useState<City[]>([]);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [cityId, setCityId] = useState<number | null>(null);
  const [activityIds, setActivityIds] = useState<number[]>([]);
  const [results, setResults] = useState<ActivityResult[]>([]);
  const [current, setCurrent] = useState<CurrentConditions[]>([]);
  const [lastFetch, setLastFetch] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const initial = parseParams();
    Promise.all([fetchCities(), fetchActivities()])
      .then(([cityList, activityList]) => {
        setCities(cityList);
        setActivities(activityList);
        setCityId(initial.city ?? cityList[0]?.id ?? null);
        setActivityIds(
          initial.activities.length > 0 ? initial.activities : activityList.map((a) => a.id),
        );
      })
      .catch(() => setError('Nije moguće dohvatiti popis gradova i aktivnosti.'));
    fetchStatus()
      .then((s) => setLastFetch(s.lastFetch))
      .catch(() => {});
  }, []);

  useEffect(() => {
    function loadCurrent() {
      fetchCurrent().then(setCurrent).catch(() => {});
    }
    loadCurrent();
    const interval = setInterval(loadCurrent, CURRENT_REFRESH_MS);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (cityId === null) return;
    writeParams(cityId, activityIds);
  }, [cityId, activityIds]);

  useEffect(() => {
    if (cityId === null || activityIds.length === 0) {
      setResults([]);
      return;
    }
    setLoading(true);
    setError(null);
    fetchSuitability(cityId, activityIds)
      .then(setResults)
      .catch(() => setError('Nije moguće dohvatiti prognozu pogodnosti.'))
      .finally(() => setLoading(false));
  }, [cityId, activityIds]);

  const toggleActivity = useCallback((id: number) => {
    setActivityIds((prev) =>
      prev.includes(id) ? prev.filter((a) => a !== id) : [...prev, id],
    );
  }, []);

  const selectedCityName = useMemo(
    () => cities.find((c) => c.id === cityId)?.name ?? '',
    [cities, cityId],
  );

  const selectedCurrent = useMemo(
    () => current.find((c) => c.cityId === cityId) ?? null,
    [current, cityId],
  );

  return (
    <div className="app">
      <header className="app-header">
        <h1>MeteoAktiv</h1>
        <p className="subtitle">Je li vrijeme pogodno za vašu aktivnost u sljedeća 3 dana?</p>
      </header>

      <section className="controls">
        <CitySelect cities={cities} selectedCityId={cityId} onChange={setCityId} />
        <ActivitySelect activities={activities} selectedIds={activityIds} onToggle={toggleActivity} />
      </section>

      {error && <p className="error-banner">{error}</p>}

      <section className="dashboard">
        <CurrentWeatherCard current={selectedCurrent} cityName={selectedCityName} />
        <CityMap cities={cities} current={current} selectedCityId={cityId} onSelect={setCityId} />
      </section>

      {loading && <p className="loading-hint">Učitavanje…</p>}

      {!loading && results.length > 0 && (
        <>
          <NowCard results={results} />
          <section className="grid-section">
            <h2>3 dana — {selectedCityName}</h2>
            <SuitabilityGrid results={results} />
          </section>
        </>
      )}

      {!loading && results.length === 0 && activityIds.length === 0 && (
        <p className="empty-hint">Odaberite bar jednu aktivnost da bi se prikazala prognoza.</p>
      )}

      <StatusFooter lastFetch={lastFetch} />
    </div>
  );
}
