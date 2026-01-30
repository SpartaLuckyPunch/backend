import http from "k6/http";
import { sleep } from "k6";

/**
 * 전체 랜덤 좌표 (Cold)
 */
function randomCoord() {
  return {
    lat: 37.0 + Math.random() * 1.3,   // 37.0 ~ 38.3
    lon: 126.3 + Math.random() * 1.6   // 126.3 ~ 127.9
  };
}

/**
 * Hot / Warm / Cold 섞기
 */
function pickLocation() {
  const r = Math.random();

  // Hot (40%)
  if (r < 0.4) {
    return { lat: 37.5, lon: 127.0 };
  }
  // Warm (40%)
  else if (r < 0.8) {
    return {
      lat: 37.5 + (Math.random() - 0.5) * 0.1,
      lon: 127.0 + (Math.random() - 0.5) * 0.1
    };
  }
  // Cold (20%)
  else {
    return randomCoord();
  }
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
  const loc = pickLocation();
  const radiusList = [1, 3, 5, 10];
  const radius = radiusList[Math.floor(Math.random() * radiusList.length)];

  const url =
      `http://host.docker.internal:8080/api/meetings/test` +
      `?latitude=${loc.lat}&longitude=${loc.lon}&distance=${radius}`;

  http.get(url);

  sleep(0.1);
}
