import http from "k6/http";
import { sleep } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
/**
 * 2026-01-30T16:24:31.69734에 생성된 토큰
 */
const JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwiZW1haWwiOiJ0ZXN0MUB0ZXN0LmNvbSIsIm5pY2tuYW1lIjoidGVzdHVzZXIxIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3Njk3NTc4NzEsImV4cCI6MTc2OTc2MTQ3MX0.c_mMiklVklfCI-E-aWLdAO9JZuWHaGc6eG8o4y6AEaA";

/**
 * 랜덤 중심 좌표 (Cold)
 */
function randomCenter() {
  return {
    lat: 37.0 + Math.random() * 1.3,
    lng: 126.3 + Math.random() * 1.6
  };
}

/**
 * ViewPort 생성
 */
function makeViewPort(centerLat, centerLng, delta) {
  return {
    centerLat,
    centerLng,
    minLat: centerLat - delta,
    maxLat: centerLat + delta,
    minLng: centerLng - delta,
    maxLng: centerLng + delta,
  };
}

/**
 * Hot / Warm / Cold ViewPort
 */
function pickViewPort() {
  const r = Math.random();

  if (r < 0.4) {
    return makeViewPort(37.5, 127.0, 0.05);
  }

  if (r < 0.8) {
    const offsetLat = (Math.random() - 0.5) * 0.05;
    const offsetLng = (Math.random() - 0.5) * 0.05;
    return makeViewPort(
        37.5 + offsetLat,
        127.0 + offsetLng,
        0.05
    );
  }

  const center = randomCenter();
  const delta = [0.02, 0.05, 0.1][Math.floor(Math.random() * 3)];
  return makeViewPort(center.lat, center.lng, delta);
}

export let options = {
  stages: [
    { duration: "30s", target: 20 },
    { duration: "1m", target: 50 },
    { duration: "1m", target: 100 },
    { duration: "30s", target: 0 },
  ],
};

export default function () {
  const vp = pickViewPort();

  const url =
      `${BASE_URL}/api/meetings/map` +
      `?centerLat=${vp.centerLat}` +
      `&centerLng=${vp.centerLng}` +
      `&minLat=${vp.minLat}` +
      `&maxLat=${vp.maxLat}` +
      `&minLng=${vp.minLng}` +
      `&maxLng=${vp.maxLng}`;

  const params = {
    headers: {
      Authorization: `Bearer ${JWT_TOKEN}`,
    },
  };

  http.get(url, params);

  sleep(0.1);
}
