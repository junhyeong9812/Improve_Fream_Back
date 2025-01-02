# Config파일 기능 정리

## 1.구성 파일 개요
- Config는 애플리케이션의 주요 설정과 사용자 정의 동작을 관리합니다. 
- 데이터베이스 쿼리, REST 클라이언트 설정, WebSocket 구성, 스케줄링 작업 등의 설정을 포함합니다.

## 2.구성 파일 설명

### 2.1 CustomWebSocketHandler
- **위치:** Fream_back.improve_Fream_Back.config.customWebSocket
- **목적:** WebSocket 세션의 생명 주기를 관리하고, Redis 키의 TTL(유효 시간)을 갱신합니다.
- **주요 기능:**
    - WebSocket 세션이 종료되면 Redis에 저장된 관련 키를 삭제합니다.
    - 클라이언트에서 PING 메시지를 보낼 경우, Redis TTL을 30분으로 갱신합니다.

### 2.2 EmailBasedUserDestinationResolver
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** 인증된 사용자의 이메일을 기반으로 STOMP 메시지 목적지를 설정합니다.
- **주요 기능:**
    - SecurityContext에서 사용자의 이메일을 추출합니다.
    - STOMP 목적지 경로를 /user/{email} 형식으로 변환하여 이메일 기반 경로를 제공합니다.

### 2.3 QueryDslConfig
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** QueryDSL을 위한 JPAQueryFactory 빈을 제공합니다.
- **주요 기능:**
    - EntityManager를 통해 QueryDSL을 쉽게 사용할 수 있도록 설정합니다.

### 2.4 RestTemplateConfig
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** REST API 호출을 위한 RestTemplate 빈을 제공합니다.
- **주요 기능:**
    - REST API와의 통신을 간편하게 하기 위한 RestTemplate 설정.

### 2.5 ShipmentStatusScheduler
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** 배송 상태를 주기적으로 갱신하는 스케줄러입니다.
- **주요 기능:**
    - 6시간마다 OrderShipmentCommandService를 호출하여 배송 상태를 업데이트합니다.
    - CRON 표현식: 0 0 */6 * * *

### 2.6 WebConfig
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** 페이징 처리를 위한 설정을 추가합니다.
- **주요 기능:**
    - PageableHandlerMethodArgumentResolver를 등록하여 페이징 처리 매개변수를 지원합니다.

### 2.7 WebSocketAuthInterceptor
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** WebSocket 연결 시 사용자의 인증 상태를 확인합니다.
- **주요 기능:**
    - WebSocket 요청에 포함된 JWT 토큰을 검증합니다.
    - 인증된 사용자 이메일을 Redis에 저장하고 TTL(30분)을 설정합니다.

### 2.8 WebSocketConfig
**(Front 완성 후 테스트 필요)**
- **위치:** Fream_back.improve_Fream_Back.config
- **목적:** WebSocket 및 STOMP 메시지 브로커를 설정합니다.
- **주요 기능:**
    - 메시지 브로커를 /topic과 /queue 경로로 설정.
    - /topic: 그룹 메시지 브로드캐스트.
    - /queue: 개별 사용자 알림.
    - 클라이언트 요청 경로를 /app으로 설정.
    - WebSocket 연결 엔드포인트를 /ws로 정의하고, SockJS를 지원.

## 3.파일 구조 
```
Fream_back.improve_Fream_Back.config
├── customWebSocket/
│   └── CustomWebSocketHandler.java
├── EmailBasedUserDestinationResolver.java
├── QueryDslConfig.java
├── RestTemplateConfig.java
├── ShipmentStatusScheduler.java
├── WebConfig.java
├── WebSocketAuthInterceptor.java
└── WebSocketConfig.java
```