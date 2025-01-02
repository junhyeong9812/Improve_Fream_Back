# Security 구성 및 설명

## 1. 개요
- 애플리케이션의 인증 및 권한 관리를 위한 설정입니다.
- JWT를 이용한 인증, Redis를 통한 화이트리스트 관리, Spring Security 설정 등을 포함합니다.

## 2. 주요 구성 요소

### 2.1 SecurityConfig
- **위치:** `Fream_back.improve_Fream_Back.user.security`
- **목적:** Spring Security의 보안 설정을 담당합니다.
- **주요 기능:**
    - JWT 인증 필터 추가
    - CORS 설정
    - 관리자와 일반 사용자 접근 권한 분리

### 2.2 JwtAuthenticationFilter
- **위치:** `Fream_back.improve_Fream_Back.user.Jwt`
- **목적:** 요청의 JWT 토큰을 검증하고 인증 객체를 설정합니다.
- **주요 기능:**
    - 토큰 유효성 검사
    - Redis 화이트리스트 확인

### 2.3 JwtTokenProvider
- **위치:** `Fream_back.improve_Fream_Back.user.Jwt`
- **목적:** JWT 생성, 검증, 디코딩을 담당합니다.
- **주요 기능:**
    - JWT 생성
    - 토큰 검증 및 이메일 추출

### 2.4 RedisService
- **위치:** `Fream_back.improve_Fream_Back.user.redis`
- **목적:** Redis를 통해 토큰의 화이트리스트를 관리합니다.
- **주요 기능:**
    - 화이트리스트 토큰 추가, 확인, 삭제

### 2.5 CustomUserDetailsService
- **위치:** `Fream_back.improve_Fream_Back.user.security`
- **목적:** Spring Security에서 사용자 정보를 로드하는 서비스입니다.
- **주요 기능:**
    - 이메일로 사용자 조회
    - 인증 정보 반환

### 2.6 SecurityUtils
- **위치:** `Fream_back.improve_Fream_Back.utils`
- **목적:** 현재 인증된 사용자의 이메일을 추출합니다.
- **주요 기능:**
    - SecurityContext에서 이메일 추출

---

## 3. 동작 방식
- 요청이 들어오면 `JwtAuthenticationFilter`가 토큰을 검증합니다.
- 토큰이 유효하면 Redis에서 화이트리스트에 있는지 확인합니다.
- 인증된 사용자의 이메일을 SecurityContext에 저장합니다.

---

## 4. 파일 구조
```plaintext
Fream_back.improve_Fream_Back.user
├── security
│   ├── SecurityConfig.java
│   ├── CustomUserDetailsService.java
├── redis
│   ├── RedisService.java
├── Jwt
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
└── utils
    └── SecurityUtils.java
