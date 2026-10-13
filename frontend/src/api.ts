import type { Activity, ActivityResult, City, CurrentConditions, StatusResponse } from './types';

const API_BASE = import.meta.env.VITE_API_BASE ?? '';

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) {
    throw new Error(`Zahtjev nije uspio (${res.status})`);
  }
  return res.json() as Promise<T>;
}

export function fetchCities(): Promise<City[]> {
  return get('/api/cities');
}

export function fetchActivities(): Promise<Activity[]> {
  return get('/api/activities');
}

export function fetchSuitability(cityId: number, activityIds: number[]): Promise<ActivityResult[]> {
  return get(`/api/suitability?cityId=${cityId}&activityIds=${activityIds.join(',')}`);
}

export function fetchStatus(): Promise<StatusResponse> {
  return get('/api/status');
}

export function fetchCurrent(): Promise<CurrentConditions[]> {
  return get('/api/current');
}
