export interface City {
  id: number;
  name: string;
  lat: number;
  lon: number;
}

export interface Activity {
  id: number;
  name: string;
  windowStart: number;
  windowEnd: number;
  minConsecutiveHours: number;
}

export interface TimeBlock {
  start: string;
  end: string;
}

export interface Reason {
  parameter: string;
  message: string;
}

export interface HourEvaluation {
  time: string;
  temperature: number | null;
  precipitation: number | null;
  windSpeed: number | null;
  cloudCover: number | null;
  hasData: boolean;
  suitable: boolean;
}

export interface DaySuitability {
  date: string;
  suitable: boolean;
  bestBlock: TimeBlock | null;
  reasons: Reason[];
  hourly: HourEvaluation[];
}

export interface ActivityResult {
  activityId: number;
  activityName: string;
  days: DaySuitability[];
  now: boolean;
}

export interface StatusResponse {
  lastFetch: string | null;
}

export interface CurrentConditions {
  cityId: number;
  cityName: string;
  lat: number;
  lon: number;
  time: string;
  temperature: number | null;
  precipitation: number | null;
  windSpeed: number | null;
  cloudCover: number | null;
  hasData: boolean;
}
