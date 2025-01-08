# Notification 엔드포인트 기능 및 로직 정리

## 1. 엔드포인트 개요
Notification(알림) 관련 작업을 수행하기 위한 엔드포인트로, 다음과 같은 주요 기능을 제공합니다:

- **알림 생성**: 단일 사용자 또는 모든 사용자에게 알림을 생성
- **알림 읽음 처리**: 특정 알림을 읽음 처리
- **알림 필터링 및 조회**: 카테고리 또는 유형별 알림 조회, 읽음 여부로 필터링
- **알림 삭제**: 알림 삭제
- **WebSocket 연결 상태 갱신**: WebSocket의 연결 상태 확인 및 TTL 갱신

---

## 2. 엔드포인트 상세 설명

### 2.1 단일 사용자 알림 생성
**엔드포인트:**
```
POST /api/notifications
```

**요청 헤더:**
- Authorization: Bearer {토큰}

**요청 데이터:**
```json
{
  "category": "SHOPPING",
  "type": "TRADE",
  "message": "새로운 거래가 발생했습니다."
}
```

**응답 데이터:**
```json
{
  "id": 1,
  "category": "SHOPPING",
  "type": "TRADE",
  "message": "새로운 거래가 발생했습니다.",
  "isRead": false,
  "createdAt": "2025-01-01T12:00:00Z"
}
```

**기능:**
1. 요청 데이터와 userId를 기반으로 알림 생성.
2. Redis에 연결된 사용자에 한해 WebSocket으로 알림 전송.
3. 생성된 알림 데이터를 반환.

---

### 2.2 모든 사용자 알림 생성
**엔드포인트:**
```
POST /api/notifications/broadcast
```

**요청 데이터:**
```json
{
  "category": "SHOPPING",
  "type": "ANNOUNCEMENT",
  "message": "중요 공지사항이 있습니다."
}
```

**응답 데이터:**
```json
[
  {
    "id": 1,
    "category": "SHOPPING",
    "type": "ANNOUNCEMENT",
    "message": "중요 공지사항이 있습니다.",
    "isRead": false,
    "createdAt": "2025-01-01T12:00:00Z"
  },
  ...
]
```

**기능:**
1. 모든 사용자에게 동일한 메시지로 알림 생성.
2. Redis에 연결된 사용자에게 WebSocket으로 알림 전송.
3. 생성된 알림 목록 반환.

---

### 2.3 알림 읽음 처리
**엔드포인트:**
```
PATCH /api/notifications/{id}/read
```

**요청 헤더:**
- Authorization: Bearer {토큰}

**응답 데이터:**
- 상태 코드 200 (성공)

**기능:**
1. 알림 ID를 기반으로 알림 데이터 조회.
2. 사용자 인증 정보를 확인하여 읽음 처리.

---

### 2.4 카테고리별 알림 조회
**엔드포인트:**
```
GET /api/notifications/filter/category
```

**요청 파라미터:**
- `category` (필수): 조회할 알림 카테고리 (e.g., `SHOPPING`)

**응답 데이터:**
```json
[
  {
    "id": 1,
    "category": "SHOPPING",
    "type": "TRADE",
    "message": "새로운 거래가 발생했습니다.",
    "isRead": false,
    "createdAt": "2025-01-01T12:00:00Z"
  },
  ...
]
```

**기능:**
1. 사용자의 이메일을 기반으로 특정 카테고리의 알림 필터링.
2. 조회된 알림 데이터를 반환.

---

### 2.5 유형별 알림 조회
**엔드포인트:**
```
GET /api/notifications/filter/type
```

**요청 파라미터:**
- `type` (필수): 조회할 알림 유형 (e.g., `TRADE`)

**응답 데이터:**
```json
[
  {
    "id": 1,
    "category": "SHOPPING",
    "type": "TRADE",
    "message": "새로운 거래가 발생했습니다.",
    "isRead": false,
    "createdAt": "2025-01-01T12:00:00Z"
  },
  ...
]
```

**기능:**
1. 사용자의 이메일을 기반으로 특정 유형의 알림 필터링.
2. 조회된 알림 데이터를 반환.

---

### 2.6 WebSocket 연결 상태 갱신 (PING 처리)
**엔드포인트:**
```
WebSocket /api/notifications/ping
```

**기능:**
1. 사용자의 이메일을 기반으로 Redis TTL 갱신.
2. TTL이 10분 이하인 경우 30분으로 연장.

---

## 3. Notification 카테고리 및 유형

### 3.1 카테고리
- `SHOPPING`: 쇼핑 관련 알림
- `STYLE`: 스타일 관련 알림

### 3.2 유형
| 유형             | 카테고리    | 설명                   |
|------------------|------------|-----------------------|
| `TRADE`         | SHOPPING   | 거래 관련 알림        |
| `BID`           | SHOPPING   | 입찰 관련 알림        |
| `STORAGE`       | SHOPPING   | 보관 관련 알림        |
| `FAVORITE`      | SHOPPING   | 즐겨찾기 관련 알림    |
| `BENEFIT`       | SHOPPING   | 혜택 관련 알림        |
| `ANNOUNCEMENT`  | SHOPPING   | 공지사항 알림         |
| `LIKE`          | STYLE      | 좋아요 알림           |
| `COMMENT`       | STYLE      | 댓글 알림             |
| `FOLLOW`        | STYLE      | 팔로우 알림           |

---

## 4. 보안 및 인증
1. **JWT 토큰 인증**:
    - `Authorization` 헤더를 통해 JWT 토큰을 전달.
    - 토큰에서 사용자 이메일을 추출하여 알림 데이터를 필터링.

2. **Redis를 통한 WebSocket 연결 확인**:
    - Redis에 사용자 연결 상태를 저장.
    - 연결된 사용자에게만 WebSocket으로 알림 전송.

---

## 5. 프론트 웹소켓 연결 로직 정의
```
   // 1) 로그인 성공 후
const token = result.data.token;  // JWT
// 2) WebSocket 연결
let socket = new SockJS('/ws?token=' + token);
stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    console.log("WebSocket connected: " + frame);

    // 3) 구독
    stompClient.subscribe('/topic/some-notice', function(msg) {
       console.log("Received: " + msg);
    });

    // 4) 주기적으로 ping
    setInterval(()=>{
       stompClient.send("/app/ping", {}, {});
    }, 30000); // 30초마다 ping
});

```


이 문서는 Notification 엔드포인트의 주요 기능과 로직, 카테고리 및 유형을 설명합니다.

