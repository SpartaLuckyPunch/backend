# 실시간 번개 모임 매칭 플랫폼, 번쩍⚡️

## 📚 프로젝트 소개

현대인들의 즉흥적이고 유연한 모임 문화를 지원하는 ***실시간 번개 모임 매칭 플랫폼, 번쩍⚡️***

> **오늘 퇴근길, 갑자기 맥주 한잔하고 싶은데 부를 사람이 없네?**

친구들과 약속 잡으려면 한 달 전부터 스케줄 조정해야 하는 피곤한 일상,
우리는 더 이상 거창한 계획이 아닌, 지금 이 순간의 내 기분에 솔직하고 싶습니다.
번쩍⚡️은 '약속'의 부담을 덜고, **'만남'의 설렘**만 남기고자 시작되었습니다.

> **계획된 모임에서 유연한 연결로, 모임의 패러다임이 바뀝니다.**

목적 중심의 동호회나 번거롭게 예약해야 하는 모임은 이제 현대인의 라이프 스타일과 맞지 않습니다.
번쩍⚡️은 MZ세대의 **자유로운 만남 추구 문화**와 **짧고 강렬한 경험**을 선호하는 트렌드에 발맞춰,
’**즉흥 모임 매칭**’을 지원합니다.

## ⭐ 핵심 기능

### 🔍 번개 조회

> *⚡️ 오늘 심심한데, 뭐 할 거 없나?! 지금 바로 번개 모임에 함께해요!*

- 현재 모집 중인 모임 조회
- 키워드 검색, 카테고리/일정 필터링, 정렬 기능 제공
- 사용자 위치 반경 5km 이내 번개 목록 제공
    - 사용자의 실시간 위치 사용
    - 미공유 시, 저장된 주소 정보 사용
- 지도 UI를 통한 조회 가능

### 🙌 번개 참여

> *⚡️ 이 번개 모임, 나도 참여하고 싶어!*

- 번개 참여 시, 해당 번개의 채팅방에 초대
- 모집 완료 시, 참여 버튼 비활성화
- 기존 참여자 참여 취소 시, 참여 버튼 활성화

### 💬 채팅

> *⚡️ 막힘없이, 실시간 소통으로 모임의 설렘을 더하다!*

- 모임별 단체 채팅방 제공
- 유저와 일대일 채팅방 제공
- 채팅방별 미확인 메시지 수 확인 가능

### 👤 개인화 피드

> *⚡️ 나보다 나를 더 잘 아는, 당신만을 위한 맞춤형 번개!*

- 사용자 개인화 피드 제공 예정
    - 사용자의 좋아요, 조회, 검색 로그 분석
    - 등록된 관심 카테고리 반영
    - 최근 참여 모임 패턴 학습

## ⚙️ 프로젝트 설계

### 📄 API 명세서

[API 명세서 By notion](https://www.notion.so/teamsparta/API-2e82dc3ef51480839f67df3cbbac444e?source=copy_link)

### 🗺️ ERD

<img width="1953" height="1230" alt="image" src="https://github.com/user-attachments/assets/bac6f823-4690-495c-9a09-15e1a8ff59f3" />

### ✏️ 와이어프레임

[와이어프레임 By Figma](https://www.figma.com/design/i6Ut7Ox0TCSVDqxJSGeGIt/LUCKY-PUNCH?node-id=0-1&t=4jhOEdD7gwRjp1fr-1)

### 🏗️ 클라우드 아키텍쳐

<img width="1065" height="670" alt="image" src="https://github.com/user-attachments/assets/37d90460-7d3d-490a-bed5-72ddf2f0049f" />


## 🤔 기술적 의사 결정(성능 개선 및 구조 개선 과정)

<details>
<summary>1️⃣ 검색 기능 고도화 (DB → Redis GEO → Elasticsearch)</summary>
<div markdown="1">

### 🎯 도입 배경

- 위치 기반 모임 조회
- 지도 조회 + 목록 조회
- 복합 조건 필터 + 정렬 필요

### 🔄 개선 단계

1. DB 직접 조회 (풀스캔)
2. 공간 인덱스 (R-Tree)
3. Redis GEO 도입
4. Elasticsearch 도입

### 🧪 테스트 환경

k6 기반 점진적 부하 테스트 수행

**검색 조건**

- C0: 위치 + 상태
- C1: 위치 + 상태 + 카테고리
- C2: 위치 + 상태 + 카테고리 + 키워드

**트래픽 패턴**

- HOT (35~40%): 인기 지역 집중 트래픽 상황
- WARM (40%): 주변 탐색 및 스크롤 이동 상황
- COLD (20~25%): 신규 지역 탐색

### 📊 목록 조회 API 결과 (p95 기준)

| 조건 | DB     | Redis | ES   |
| -- | ------ | ----- | ---- |
| C0 | 13.16s | 8.09s | 29ms |
| C1 | 2.01s  | 5.74s | 39ms |
| C2 | 1.08s  | 5.58s | 20ms |

**성능 개선**

- DB 대비 ES p95 98.6% 감소
- 처리량 21.6배 증가
- 조건 증가에도 응답시간 안정 유지

### 🗺 지도 조회 API 결과 (p95 기준)

| 조건 | DB    | Redis | ES   |
| -- | ----- | ----- | ---- |
| C0 | 8.33s | 1.3s  | 58ms |
| C1 | 1.15s | 872ms | 29ms |
| C2 | 816ms | 848ms | 25ms |

**개선 효과**

- DB 대비 ES p95 97.9% 감소
- 평균 처리량 13.6배 증가
- 검색 부하 DB → ES 분리

### ⚖️ 최종 의사 결정

**비용 증가**

- 인프라 비용 증가
- 운영 복잡도 증가
- 정합성 관리 필요

**구조적 이점**

- 검색 부하 DB에서 완전 분리
- p95 응답시간 약 98% 감소
- 트래픽 증가 시 확장성 확보
- 검색 전용 엔진 구조적 우위

> 비용과 복잡도는 증가했지만
> 구조적 병목 제거와 확장성을 우선 선택

</div>
</details>

<details>
<summary>2️⃣ 이미지 조회 구조 개선 (S3 → CloudFront CDN)</summary>
<div markdown="1">

### 🎯 도입 배경

- 번개 모임 상세 조회 시 이미지 로딩 빈번
- 기존 구조: Client → S3 직접 요청

### 🐞 기존 구조 문제점

- 동일 이미지 요청에도 매번 S3 접근
- 트래픽 증가 시 응답 지연 가능
- S3 Public 설정 필요
- 별도 접근 제어 구현 필요

### 💡 개선 구조 – CloudFront CDN 도입

**개선 포인트**

- 캐시 HIT 시 엣지에서 직접 응답
- S3 요청 감소
- 트래픽 엣지 분산
- OAC/OAI로 S3 Private 유지
- HTTPS 정책 강제 적용 가능

### 📊 성능 비교

**응답 시간 테스트 (100회 반복)**

- CloudFront 평균: 149ms
- S3 평균: 187ms
- 응답 변동성: CloudFront 3배 감소

**성능 개선**

- 평균 응답시간 20% 감소
- 응답 일관성 2.3배 향상

### 💰 비용 비교 (서울 리전 기준 시나리오)

- 월 MAU: 10,000명
- 이미지 총 트래픽: 750GB

| 구분         | 비용     |
| ---------- | ------ |
| S3         | $68.10 |
| CloudFront | $91.92 |

➡ 약 35% 비용 증가

### ✅ 최종 의사 결정

비용 증가 vs 구조적 이점

- S3 직접 접근 차단
- CDN 레벨 보안 제어
- 트래픽 급증 시 엣지 분산
- 확장 가능한 정적 리소스 구조 확보

> 성능 개선은 제한적이었지만
> 보안과 구조적 안정성을 더 중요하게 판단

</div>
</details>

<details>
<summary>3️⃣ 실시간 채팅 구조 개선 (Unread 카운트 & Redis Pipeline)</summary>
<div markdown="1">

### 🎯 도입 배경

- 1:1 채팅, 모임 단체 채팅, 메시지 읽음 처리 기능 구현
- WebSocket + STOMP 기반 실시간 양방향 통신
- JWT 기반 인증 통합

### 🐞 기존 문제 – 메시지 단위 unread 관리 구조

**기존 방식**

- ChatMessage에 unreadCount 필드 보유
- 메시지 생성 시 채팅방 인원 수만큼 unreadCount 세팅
- 사용자가 메시지 확인 시 모든 메시지 UPDATE

**구조적 한계**

- 메시지 읽을 때마다 다수 UPDATE 발생
- 메시지 수 증가 → DB 부하 증가
- 채팅방 입장 시 대량 Update 쿼리 실행
- DB Write Lock 발생 가능

### 💡 개선 방향 – Sequence 기반 설계 (책갈피 시스템)

**핵심 개념**

- Global Sequence : 채팅방 전체 메시지 수
- User LastRead : 사용자가 마지막으로 읽은 메시지 번호

**계산 방식**

- 채팅방 미확인 수 = GlobalSeq - LastRead
- 메시지별 미확인 인원 = LastRead < MessageSeq 사용자 수

**개선 효과**

- 메시지 UPDATE 제거
- 읽음 처리를 "계산" 방식으로 변경
- DB Write 감소 → 구조적 확장성 확보

### ⚡️ 성능 개선 1 – DB 상태 저장 vs Redis 위치 계산

**테스트 환경**

- k6 부하 테스트
- 50 VUs
- 1초당 1회 API 호출

**결과 (p95 기준)**

| 구분          | avg   | p95   | req/s |
| ----------- | ----- | ----- | ----- |
| DB 상태 저장    | 4.84s | 5.74s | 8.16  |
| Redis 위치 계산 | 15ms  | 16ms  | 49.2  |

**📊 성능 개선**

- p95 응답시간 99.7% 감소
- 처리량 5.7배 증가
- DB Write Lock 제거
- 사용자 체감 지연 해소

### 🚀 성능 개선 2 – Redis Pipeline 도입

**문제**

- 채팅방 수만큼 Redis 요청 발생
- RTT 증가
- Thread 대기 시간 증가
- TPS 감소

**해결 방법**

- Redis Pipeline 적용
- 여러 GET 요청을 한 번에 전송
- 내부적으로 순차 처리
- 네트워크 왕복(RTT) 1회로 감소

**결과 (p95 기준)**

| 구분       | avg     | p95     | req/s |
| -------- | ------- | ------- | ----- |
| 개별 요청    | 44.69ms | 66.78ms | 47.66 |
| Pipeline | 11.56ms | 12.18ms | 49.38 |

**📊 개선 효과**

- p95 응답시간 81.7% 감소
- 네트워크 병목 제거
- 동일 로직에서도 통신 방식에 따라 성능 차이 검증

### ✅ 최종 의사 결정

- 메시지 단위 상태 관리 → Sequence 기반 구조 변경
- DB 중심 처리 → Redis 기반 계산 방식 전환
- Redis Pipeline으로 네트워크 병목 제거
- 구조적 확장성 + 실시간 성능 확보

</div>
</details>


## ⚠️ 트러블 슈팅

<details>
<summary>1️⃣ 프론트-백엔드 연결 시 발생한 CORS 설정 문제</summary>
<div markdown="1">

### 🐞 문제 상황

React(프론트)와 Spring Boot/Node.js(백엔드) 분리 개발 중 CORS 에러로 데이터 통신 차단

### 🔍 원인 분석

- 브라우저가 보안상의 이유로 다른 도메인 요청 차단
- Cross-Origin Resource Sharing 정책 위반

### 🛠 해결 과정

**❌ 1차 시도: 와일드카드 허용**

- 빠른 해결 가능
- 하지만 보안 취약점 발생
- 사용자 위치/실시간 모임 정보가 핵심인 서비스 특성상 부적합

**❌ 2차 시도: 프록시 설정**

- 개발 단계에서는 빠른 해결
- 배포 환경 도메인 구조를 고려하면 근본 해결 아님

**✅ 최종 해결 방법: 서버 측 화이트리스트 방식**

- 허용 도메인 명확히 리스트화
- 인가된 요청만 허용

세부 설정 전략
- GET/POST 등 필요한 HTTP 메서드만 허용
- 공격 표면 최소화
- Credentials 허용 정책 설정
- Cookie/Token 기반 인증 연동 고려

### 🌱 결과 및 회고

- 보안 강화: 무분별한 외부 접근 차단
- 사용자 데이터 보호 기반 마련
- 프론트-백엔드 간 명확한 통신 규약 확립
- 협업 안정성 향상

</div>
</details>

<details>
<summary>2️⃣ X-lock과 트랜잭션 단위로 인한 Lock wait timeout</summary>
<div markdown="1">

### 🐞 문제 상황

- 모임 생성 시, 해당 주최자를 팔로우하는 사용자들에게 Notification 생성
- 트랜잭션이 제한 시간 내에 행/테이블 Lock을 얻지 못해 `MySQLTransactionRollbackException: Lock wait timeout exceeded` 예외 발생

### 🔍 원인 분석

<img width="427" height="445" alt="스크린샷 2026-02-24 오전 10 15 20" src="https://github.com/user-attachments/assets/9ecf5dfb-7b59-469f-837a-251a198d2d7e" />

1. 트랜잭션 A → commit 이전이므로 X-lock 유지
2. 트랜잭션 B → FK 검증 위해 S-lock 요청
3. X-lock과 S-lock 충돌 (공존 불가)
4. 트랜잭션 B가 대기

→ Lock wait timeout 발생

### 🛠 해결 과정

**❌ 1차 시도: 서비스 퍼사드 패턴**

``` java
public MeetingCreateResponse createMeetingAndNotify(
    AuthUser authUser,
    MeetingCreateRequest request
) {
    User user = userRepository.findActivateUserById(authUser.getId());

    Meeting meeting = createMeeting(user, request);

    notificationService.notifyNewFollowerPost(meeting, user);

    return MeetingCreateResponse.from(meeting);
}
```

한계점
- 서비스 간 강한 결합
- Notification 실패 시 전체 흐름 영향


**✅ 최종 해결 방법: 이벤트 기반 처리**
``` java
@Transactional
public MeetingCreateResponse createMeeting() {

    meetingRepository.save(meeting);

    publisher.publishEvent(meetingCreatedEvent);
}
@TransactionalEventListener(phase = AFTER_COMMIT)
public void handle(MeetingCreatedEvent event) {
    notificationService.notify(...);
}
```

- AFTER_COMMIT 옵션
- 트랜잭션 커밋 이후 실행
- 서비스 간 결합도 감소

### 🌱 결과 및 회고

- 트랜잭션 분리 성공
- Lock 충돌 제거
- 안정적인 알림 처리 구조 확보
- 이벤트 기반 아키텍처의 필요성 체감

</div>
</details>

<details>
<summary>3️⃣ OSIV 설정으로 인한 SSE의 DB 커넥션 풀 장악</summary>
<div markdown="1">

### 🐞 문제 상황

- SSE가 연결된 후, HikariCP 커넥션이 반환되지 않음(Apparent connection leak detected 경고 발생)
- SSE 연결 10개 이상 생성 시 maximumPoolSize=10 커넥션 풀이 모두 점유됨
- 일반 API 요청이 DB 커넥션을 획득하지 못해 지연 발생

### 🔍 원인 분석

**OSIV(Open Session In View) 동작 방식**

- HTTP 요청 시작 시 영속성 컨텍스트 생성
- 요청이 끝날 때까지 유지
- 트랜잭션 종료 후에도 세션 유지
- 스프링 기본 설정: true

```java
// SSE 구독 Service
public SseEmitter subscribe(AuthUser authUser) {

    // DB 조회
    LocalDateTime sevenDaysAgo =
        LocalDate.now().atStartOfDay().minusDays(7);

    long unread =
        notificationRepository
            .countUnReadNotificationsInSevenDaysByUserId(userId, sevenDaysAgo);

    sseNotifyService.send(
        emitter,
        userId,
        NotificationSseResponse.sseConnection(unread)
    );

    redisMessageService.subscribe(userId);
    return emitter;
}
```

**문제의 흐름**

1. SSE 연결
2. DB 조회 (DB 커넥션 획득)
3. SSE 연결이 열려 있는 동안 영속성 컨텍스트 유지
4. DB 커넥션 반환 ❌

→ 결과: 커넥션 풀 고갈

### 🛠 해결 방법

✅ OSIV 비활성화
``` java
spring:
  jpa:
    open-in-view: false
```

- 과거 전통적인 MVC 환경에서는 JSP, Thymeleaf에서 엔티티에 직접 접근하거나 View에서 연관 객체 참조하는 경우가 있었음
  → 이때, LazyInitializationException을 방지하기 위해 OSIV 설정이 추가됨
- '번쩍' 프로젝트 구조
  - REST API 중심
  - DTO 계층 분리
  - 서비스 계층에서 데이터 가공 완료
  - View에서 엔티티 직접 접근 거의 없음
  → Lazy Loading을 Controller에서 할 필요 X
→ 따라서, OSIV 설정을 비활성화해도 무방하다고 판단함

### 🌱 결과 및 회고

- 트랜잭션 종료 시점에 즉시 커넥션 반환
- HTTP 연결 유지 여부와 무관하게 DB 세션 종료
- SSE 연결과 DB 커넥션 완전 분리
- 커넥션 풀 고갈 문제 해결

</div>
</details>


## 🛠️ 기술 스택

<img width="731" height="576" alt="스크린샷 2026-02-24 오전 9 50 47" src="https://github.com/user-attachments/assets/d57f9d15-2afc-443f-8c73-9ca9c276dd0e" />

## 🧑‍🤝‍🧑 팀원 소개

| 구성원    | 사진  | 깃허브       | 담당    |
| ------- | ---- | ---------- | ------------------ |
| 김재혁 </br> 👑팀장 | <img src="https://github.com/user-attachments/assets/730e9800-c7d0-4eaa-962a-3edbf8022702" width="100px" style="border-radius:50%;" alt="김재혁"/> | [GitHub](https://github.com/thugi_98) | 📌 유저 프로필 관리 <br/> 📌 로깅 및 모니터링 <br/> 📌 CI/CD <br/> 📌 배포 |
| 이상무 </br> 👑부팀장 | <img src="https://github.com/user-attachments/assets/2cd28adf-19cd-4d9d-8c4d-b0c5c7abe7bc" width="100px" style="border-radius:50%;" alt="이상무"/> | [GitHub](https://github.com/sangmu0502) | 📌 모임 CRUD <br/> 📌 팔로우/좋아요 CRUD <br/> 📌 실시간 채팅 기능 <br/> 📌 프론트엔드 |
| 윤종민 </br> 🎖️팀원 | <img src="https://github.com/user-attachments/assets/d5fd31a6-ba14-4e0a-a34c-80f39cfa89c4" width="100px" style="border-radius:50%;" alt="윤종민"/> | [GitHub](https://github.com/dbswhdals2002z-lang) | 📌 후기 CRUD <br/> 📌 이메일 인증 및 소셜 로그인 <br/> 📌 모임 참여 동시성 제어 |
| 이서연 </br> 🎖️팀원 | <img src="https://github.com/user-attachments/assets/b96ba557-cda8-4ea5-8776-40b637eaa40f" width="100px" style="border-radius:50%;" alt="이서연"/> | [GitHub](https://github.com/bellalee0) | 📌 인증/인가 <br/> 📌 유저 CRUD <br/> 📌 모임 참여 CRUD <br/> 📌 검색 기능 <br/> 📌 실시간 알림 기능 <br/> 📌 배포 |
