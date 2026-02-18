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
 * 위치 분포 (Hot 40 / Warm 40 / Cold 20)
 */
function pickLocation() {
  const r = Math.random();

  if (r < 0.4) {
    return {
      type: "hot",
      lat: 37.5,
      lon: 127.0
    };
  }

  if (r < 0.8) {
    return {
      type: "warm",
      lat: 37.5 + (Math.random() - 0.5) * 0.1,
      lon: 127.0 + (Math.random() - 0.5) * 0.1
    };
  }

  return {
    type: "cold",
    lat: 36.8 + Math.random() * 1.5,
    lon: 126.2 + Math.random() * 1.8
  };
}

/**
 * C0 ~ C2 조건 조합 생성
 */
function buildQueryParams(loc) {

  const radiusList = [1, 3, 5, 10];
  const radius = radiusList[Math.floor(Math.random() * radiusList.length)];

  let params = [];

  // 공통: 위치
  params.push(`latitude=${loc.lat}`);
  params.push(`longitude=${loc.lon}`);
  params.push(`distance=${radius}`);

  // C1: 카테고리
  if (["C1", "C2"].includes(CONDITION)) {
    const category = CATEGORY_LIST[Math.floor(Math.random() * CATEGORY_LIST.length)];
    params.push(`category=${category}`);
  }

  // C2: 키워드 검색 추가
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

  const loc = pickLocation();
  const query = buildQueryParams(loc);

  const url = `${BASE_URL}/test/${ARCH}/list?${query}`;

  http.get(url, {
    tags: {
      arch: ARCH,
      condition: CONDITION,
      location_type: loc.type,
      api: "LIST"
    }
  });

  sleep(0.1);
}


