export interface WeatherSummary {
  icon: string;
  label: string;
}

export function summarize(cloudCover: number | null, precipitation: number | null): WeatherSummary {
  if (precipitation !== null && precipitation >= 0.3) {
    return { icon: '🌧️', label: 'Kiša' };
  }
  if (cloudCover === null) {
    return { icon: '❔', label: 'Nepoznato' };
  }
  if (cloudCover < 20) {
    return { icon: '☀️', label: 'Vedro' };
  }
  if (cloudCover < 50) {
    return { icon: '🌤️', label: 'Pretežno vedro' };
  }
  if (cloudCover < 80) {
    return { icon: '⛅', label: 'Djelomično oblačno' };
  }
  return { icon: '☁️', label: 'Oblačno' };
}

const TEMP_STOPS: [number, [number, number, number]][] = [
  [-10, [60, 110, 220]],
  [0, [80, 150, 230]],
  [10, [100, 190, 170]],
  [18, [230, 185, 60]],
  [26, [230, 120, 50]],
  [34, [210, 60, 50]],
];

function lerp(a: number, b: number, t: number): number {
  return a + (b - a) * t;
}

export function tempColor(temp: number | null): string {
  if (temp === null) return '#8fa3a1';
  if (temp <= TEMP_STOPS[0][0]) return rgb(TEMP_STOPS[0][1]);
  for (let i = 0; i < TEMP_STOPS.length - 1; i++) {
    const [t0, c0] = TEMP_STOPS[i];
    const [t1, c1] = TEMP_STOPS[i + 1];
    if (temp >= t0 && temp <= t1) {
      const t = (temp - t0) / (t1 - t0);
      return rgb([lerp(c0[0], c1[0], t), lerp(c0[1], c1[1], t), lerp(c0[2], c1[2], t)]);
    }
  }
  return rgb(TEMP_STOPS[TEMP_STOPS.length - 1][1]);
}

function rgb([r, g, b]: [number, number, number]): string {
  return `rgb(${Math.round(r)}, ${Math.round(g)}, ${Math.round(b)})`;
}
