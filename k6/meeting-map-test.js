import http from "k6/http";
import { sleep } from "k6";

const BASE_URL = "http://host.docker.internal:8080";
const ARCH = __ENV.ARCH || "db";        // db / redis / es
const CONDITION = __ENV.CONDITION || "C0"; // C0 / C1 / C2

const CATEGORY_LIST = [
  "sports", "food", "drink", "entertainment",
  "study", "music", "culture", "etc"
];

const KEYWORDS = [
  "번개", "스터디", "러닝", "카페",
  "영화", "전시", "독서", "맛집"
];

/**
 * Viewport 생성
 * Hot 35% / Warm 40% / Cold 25%
 */
function pickViewport() {
  const r = Math.random();

  // 🔥 Hot (고정 viewport)
  if (r < 0.35) {
    return buildViewport(37.5, 127.0, 0.02, "hot");
  }

  // 🌡 Warm (소폭 이동)
  if (r < 0.75) {
    const lat = 37.5 + (Math.random() - 0.5) * 0.05;
    const lng = 127.0 + (Math.random() - 0.5) * 0.05;
    return buildViewport(lat, lng, 0.02, "warm");
  }

  // ❄ Cold (완전 다른 지역)
  const lat = 35.5 + Math.random() * 3.0;
  const lng = 126.0 + Math.random() * 3.0;
  return buildViewport(lat, lng, 0.05, "cold");
}

/**
 * 중심점 + delta 기반 viewport 계산
 */
function buildViewport(centerLat, centerLng, delta, type) {
  return {
    type,
    centerLat,
    centerLng,
    minLat: centerLat - delta,
    maxLat: centerLat + delta,
    minLng: centerLng - delta,
    maxLng: centerLng + delta
  };
}

/**
 * C0 ~ C2 조건 조합
 */
function buildQueryParams(vp) {

  let params = [];

  // Viewport 필수
  params.push(`centerLat=${vp.centerLat}`);
  params.push(`centerLng=${vp.centerLng}`);
  params.push(`minLat=${vp.minLat}`);
  params.push(`maxLat=${vp.maxLat}`);
  params.push(`minLng=${vp.minLng}`);
  params.push(`maxLng=${vp.maxLng}`);

  // C1: category 추가
  if (["C1", "C2"].includes(CONDITION)) {
    const category = CATEGORY_LIST[Math.floor(Math.random() * CATEGORY_LIST.length)];
    params.push(`category=${category}`);
  }

  // C2: keyword 추가
  if (CONDITION === "C2") {
    const keyword = KEYWORDS[Math.floor(Math.random() * KEYWORDS.length)];
    params.push(`keyword=${encodeURIComponent(keyword)}`);
  }

  return params.join("&");
}

export const options = {
  stages: [
    { duration: "30s", target: 20 },
    { duration: "1m", target: 50 },
    { duration: "1m", target: 100 },
    { duration: "30s", target: 0 }
  ],
  thresholds: {
    http_req_duration: ["p(95)<2000"],
    http_req_failed: ["rate<0.01"]
  }
};

export default function () {

  const viewport = pickViewport();
  const query = buildQueryParams(viewport);

  const url = `${BASE_URL}/test/${ARCH}/map?${query}`;

  http.get(url, {
    tags: {
      arch: ARCH,
      condition: CONDITION,
      viewport_type: viewport.type,
      api: "MAP"
    }
  });

  sleep(0.1);
}
