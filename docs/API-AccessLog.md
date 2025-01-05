# Access Log 기능 정리

## 1. 기능 개요

- **Access Log**는 사용자 접속 정보를 기록하기 위한 기능입니다.
- 사용자의 위치, 디바이스, 네트워크 정보와 같은 다양한 데이터를 수집하여 저장합니다.
- 저장된 데이터는 분석 및 통계 목적으로 활용될 수 있습니다.

## 2. 주요 엔티티 및 DTO 설명

### 2.1 UserAccessLog 엔티티

- **위치:** `Fream_back.improve_Fream_Back.accessLog.entity.UserAccessLog`
- **목적:** 사용자 접속 데이터를 저장하기 위한 엔티티.
- **주요 필드:**
    - `id`: 고유 식별자.
    - `refererUrl`: 참조 URL.
    - `userAgent`: 브라우저 및 OS 정보.
    - `os`: 운영체제 정보.
    - `browser`: 브라우저 정보.
    - `deviceType`: 디바이스 타입 (Mobile, Desktop 등).
    - `accessTime`: 접속 시간.
    - `ipAddress`: 사용자 IP 주소.
    - `country`, `region`, `city`: 위치 정보.
    - `pageUrl`: 방문한 페이지 URL.
    - `email`: 사용자 이메일 (익명 사용자는 "Anonymous"로 저장).
    - `isAnonymous`: 익명 사용자 여부.
    - `networkType`: 네트워크 타입 (WiFi, LTE 등).
    - `browserLanguage`: 브라우저 언어.
    - `screenWidth`, `screenHeight`, `devicePixelRatio`: 화면 구성 데이터.

### 2.2 UserAccessLogDto

- **위치:** `Fream_back.improve_Fream_Back.accessLog.dto.UserAccessLogDto`
- **목적:** 사용자 요청 데이터를 캡슐화하여 서비스로 전달하기 위한 DTO.
- **주요 필드:**
    - UserAccessLog 엔티티와 동일한 필드 구조.
    - Controller에서 사용자 입력 데이터를 수집하고, Service 계층으로 전달.

## 3. 주요 서비스 설명

### 3.1 GeoIPService

- **위치:** `Fream_back.improve_Fream_Back.accessLog.service.GeoIPService`
- **목적:** 사용자 IP를 기반으로 위치 정보를 조회.
- **주요 기능:**
    - MaxMind의 GeoLite2-City.mmdb를 사용하여 위치 정보 조회.
    - `getLocation(String ip)`: IP 주소를 기반으로 국가, 지역, 도시 정보를 반환.
    - 조회 실패 시 기본값으로 "Unknown" 반환.

### 3.2 UserAccessLogCommandService

- **위치:** `Fream_back.improve_Fream_Back.accessLog.service.UserAccessLogCommandService`
- **목적:** 사용자 접속 로그 데이터를 생성 및 저장.
- **주요 기능:**
    - `createAccessLog(UserAccessLogDto logDto)`: DTO 데이터를 엔티티로 변환하여 저장.
    - GeoIPService를 활용하여 IP 기반 위치 정보를 설정.
    - 이메일이 없을 경우 익명 사용자로 설정.

## 4. Controller 설명

### 4.1 UserAccessLogCommandController

- **위치:** `Fream_back.improve_Fream_Back.accessLog.controller.UserAccessLogCommandController`
- **목적:** 사용자 요청을 받아 Access Log 데이터를 처리.
- **주요 엔드포인트:**
    - `POST /access-log/create`
        - **입력:** UserAccessLogDto
        - **기능:**
            - HTTP 요청 헤더에서 IP 주소, User-Agent, Referer URL 추출.
            - DTO 데이터를 CommandService에 전달하여 저장.

## 5. 파일 구조

```
Fream_back.improve_Fream_Back.accessLog
├── controller
│   └── UserAccessLogCommandController.java
├── dto
│   └── UserAccessLogDto.java
├── entity
│   └── UserAccessLog.java
├── repository
│   └── UserAccessLogRepository.java
└── service
    ├── GeoIPService.java
    └── UserAccessLogCommandService.java
```

## 6. 데이터 처리 흐름

1. **클라이언트 요청:**
    - 사용자가 페이지에 접속하면 요청 데이터를 백엔드로 전송.
2. **Controller:**
    - HTTP 헤더에서 IP, User-Agent, Referer URL 데이터를 추출하여 DTO에 매핑.
3. **Service:**
    - GeoIPService를 활용하여 IP 기반 위치 정보를 설정.
    - 엔티티를 생성하고 데이터베이스에 저장.
4. **저장:**
    - 데이터베이스에 저장된 로그는 통계 및 분석에 활용 가능.

## 7. 추가 정보

- **GeoLite2-City.mmdb 파일:**
    - MaxMind에서 제공하는 무료 데이터베이스로, IP 기반 위치 정보를 조회.
    - 리소스 폴더에 포함되어야 하며, GeoIPService에서 초기화.
- **익명 사용자 처리:**
    - 이메일이 없는 경우 "Anonymous"로 저장.
- **화면 구성 데이터:**
    - 사용자의 디바이스 특성을 분석하여 UX 최적화 가능.

