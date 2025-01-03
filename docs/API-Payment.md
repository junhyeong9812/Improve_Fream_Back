# PaymentInfo 엔드포인트 기능 및 로직 정리

## 1. 엔드포인트 개요
PaymentInfo와 관련된 작업을 수행하기 위한 엔드포인트로, 다음과 같은 주요 기능을 제공합니다:
- **결제 정보 생성**: 사용자 결제 정보 등록 (결제 및 환불 과정 포함)
- **결제 정보 삭제**: 특정 결제 정보 삭제
- **결제 정보 목록 조회**: 사용자의 모든 결제 정보 조회
- **결제 정보 단일 조회**: 특정 결제 정보 상세 조회
- **테스트 결제 및 환불**: 카드 유효성 테스트를 위한 결제 및 환불 처리

이 엔드포인트는 보안을 위해 JWT 토큰을 사용하며, 개별 작업은 사용자 이메일을 기반으로 인증 및 검증이 수행됩니다. 아이폰에서 결제 정보 생성 시 사용자 카드 유효성을 확인하기 위해 소액 결제 및 환불 과정을 포함합니다.

---

## 2. 엔드포인트 상세 설명

### 2.1 결제 정보 생성
**엔드포인트:**
```
POST /api/payment-info
```

**필요 권한:** 로그인 사용자

**요청 헤더:**
- Authorization: Bearer {토큰}

**요청 데이터:**
```json
{
   "cardNumber": "1234567890123456",
   "cardPassword": "12",
   "expirationDate": "2025-01",
   "birthDate": "19900101"
}
```

**응답 데이터:**
```json
{
   "message": "결제 정보가 성공적으로 생성되었습니다."
}
```

**기능:**
1. **사용자 인증**:
    - SecurityContextHolder에서 JWT 토큰을 기반으로 사용자 이메일을 출출.
2. **결제 정보 유효성 검증**:
    - 사용자가 등록 가능한 결제 정보 개수 제한(최대 5개).
3. **카드 유효성 확인**:
    - 소액 결제 요청 및 환불을 통해 카드 유효성을 검증.
    - PortOne API를 사용하여 실제 결제 및 환불 수행.
4. **결제 정보 저장**:
    - 유효성 검증이 완료된 결제 정보를 저장.

---

### 2.2 결제 정보 삭제
**엔드포인트:**
```
DELETE /api/payment-info/{id}
```

**필요 권한:** 로그인 사용자

**요청 헤더:**
- Authorization: Bearer {토큰}

**응답 데이터:**
```json
{
   "message": "결제 정보가 성공적으로 삭제되었습니다."
}
```

**기능:**
1. **사용자 인증**:
    - SecurityContextHolder에서 JWT 토큰을 기반으로 사용자 이메일을 출출.
2. **결제 정보 삭제**:
    - 사용자 계열과 연결된 결제 정보 ID를 확인하고 삭제.

---

### 2.3 결제 정보 목록 조회
**엔드포인트:**
```
GET /api/payment-info
```

**필요 권한:** 로그인 사용자

**요청 헤더:**
- Authorization: Bearer {토큰}

**응답 데이터:**
```json
[
   {
      "id": 1,
      "cardNumber": "123456******3456",
      "cardPassword": "**",
      "expirationDate": "2025-01",
      "birthDate": "19900101"
   },
   ...
]
```

**기능:**
1. **사용자 인증**:
    - SecurityContextHolder에서 JWT 토큰을 기반으로 사용자 이메일을 출출.
2. **결제 정보 조회**:
    - 사용자 계열에 연결된 모든 결제 정보를 반환.

---

### 2.4 결제 정보 단일 조회
**엔드포인트:**
```
GET /api/payment-info/{id}
```

**필요 권한:** 로그인 사용자

**요청 헤더:**
- Authorization: Bearer {토큰}

**응답 데이터:**
```json
{
   "id": 1,
   "cardNumber": "123456******3456",
   "cardPassword": "**",
   "expirationDate": "2025-01",
   "birthDate": "19900101"
}
```

**기능:**
1. **사용자 인증**:
    - SecurityContextHolder에서 JWT 토큰을 기반으로 사용자 이메일을 출출.
2. **결제 정보 조회**:
    - ID를 기반으로 특정 결제 정보를 반환.

---

### 2.5 테스트 결제 및 환불
**엔드포인트:**
```
POST /api/payment-info/test-payment
```

**필요 권한:** 로그인 사용자

**요청 헤더:**
- Authorization: Bearer {토큰}

**요청 데이터:**
```json
{
   "cardNumber": "1234567890123456",
   "cardPassword": "12",
   "expirationDate": "2025-01",
   "birthDate": "19900101"
}
```

**응답 데이터:**
```json
{
   "message": "테스트 결제와 환불이 성공적으로 수행되었습니다. 거래 고유번호: imp_1234567890"
}
```

**기능:**
1. **카드 유효성 테스트**:
    - PortOne API를 사용하여 100원의 소액 결제를 수행.
    - 성공 시, 환불 요청을 통해 결제 정보가 유효한 카드임을 확인.
2. **환불 처리 검증**:
    - 환불 성공 역시를 확인하여 테스트 결과 반환.

---

## 3. 보안 및 인증

1. **사용자 인증**:
    - `Authorization` 헤더에서 JWT 토큰을 추출하여 사용자 이메일을 인증합니다.
    - Spring Security의 `SecurityContextHolder`를 사용하여 인증 정보를 관리하며, 인증된 사용자만 엔드포인트에 접근할 수 있습니다.

2. **결제 정보 유효성 검증**:
    - 사용자가 등록 가능한 결제 정보의 개수를 5개로 제한합니다.
    - 각 결제 정보 생성 시, 기존 등록 정보를 확인하고 추가 등록 가능 여부를 검증합니다.

3. **카드 유효성 테스트**:
    - 결제 정보 등록 시, PortOne API를 이용해 100원의 소액 결제를 요청하여 카드 유효성을 확인합니다.
    - 소액 결제 후 즉시 환불 요청을 처리하여 테스트를 완료합니다.

4. **데이터 암호화**:
    - 결제 정보는 저장 전에 민감 데이터를 암호화하여 보관합니다.
    - 카드 번호 등 민감한 데이터는 복호화 없이 특정 포맷(`****-****-****-3456`)으로만 표시됩니다.

5. **액세스 제어**:
    - 사용자 계정과 연결된 결제 정보만 접근 및 수정할 수 있도록 제한합니다.
    - 각 엔드포인트는 사용자 인증 외에도 데이터 접근 권한을 추가로 확인합니다.

6. **로그 기록**:
    - 결제 요청 및 환불 관련 이벤트를 모두 서버 로그에 기록하여 추적 가능성을 확보합니다.
    - 비정상적인 접근이나 요청이 발생할 경우 관리자에게 알림을 보냅니다.

7. **에러 처리**:
    - 인증 실패 시 적절한 상태 코드(401 Unauthorized)를 반환합니다.
    - 결제 실패나 환불 실패와 같은 경우 사용자 친화적인 오류 메시지를 반환합니다.

