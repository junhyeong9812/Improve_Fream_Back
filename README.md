Fream 개선 프로젝트
<br>
기존의 Kream클론 프로젝트에서 부족한 점을 개선하기 위한 프로젝트
<br>
기존 프로젝트 : https://github.com/junhyeong9812/fream_back
<br>
기본 API
<br>
SpringBoot3.1
SpringDataJpa
<br>
- 1차 프로젝트 :24년 11월 11일~ 24년 11월 22일
- 1차 프로젝트 :
  - 로그인 
  - 회원가입
  - 사용자 아이디 찾기
  - 비밀번호 재설정
  - jwt 설정
  - 배송지 추가
  - 배송지 삭제
  - 배송지 수정
  - 배송지 조회
  - 상품 이미지 임시 url생성
  - 상품 생성
  - 상품 수정
  - 상품 삭제
  - 상품 단일 조회
  - 상품 필터링 조회

- 2차 프로젝트 :24년 11월 25일~ 24년 12월 6일
- 2차 프로젝트 :
  -주문,결제내역,쉽먼트,스타일 엔티티 구현
  -주문 생성
  -주문 상세 조회
  -주문 결제 완료 및 배송 준비 상태로 업데이트
  -배송 상태 업데이트
  -결제 환불 처리
  -주문 목록 조회 (필터링 지원)
  -주문 결제 및 배송 정보 조회
  -특정 주문의 결제 정보 조회
  -특정 주문의 배송 정보 조회
  - style 구현을 통한 짧은 영상 및 사진 공유 api 구현(완)

- 2차 프로렉트 미완성 부분 정리 :
  - portone연동을 통한 실제 구매 및 판매(미완)
  - 오더엔티티 portone api에 맞도록 재구성(미완)
  - 사이트 접속 통계 수집 api 구현(미완)
 
- 3차 프로젝트 :24년 12월 9일~24년 12월 13일
- 3차 프로젝트 예정 :
  - portone연동을 통한 실제 구매 및 판매
  - 오더엔티티 portone api에 맞도록 재구성
  - 사이트 접속 통계 수집 api 구현
  - 도커 및 마이크로서비스 아키텍처 도입
  - AWS 배포
  - 추가적인 성능 최적화
  - Elasticsearch 도입해보기
  - 대규모 트래픽 처리를 위한 로드 밸런싱 및 캐싱 전략 도입

<br>
기본 ERD구조
<br>
<img src="https://github.com/user-attachments/assets/c040190e-b830-451d-a873-f0c0640dc05f"/>
<br>



# API 명세서

---

## 1. 로그인 엔드포인트
- **URL**: `/api/users/login`
- **Method**: `POST`
- **Description**: 사용자의 `loginId`와 `password`로 로그인. 성공 시 쿠키에 `loginId`를 저장하고 사용자 정보를 반환.

### Request Body
```json
{
  "loginId": "string",
  "password": "string"
}
```

### Response
- Status Code: **200 OK**
  ```json
  {
  "message": "Login successful.",
  "loginId": "string",
  "nickname": "string"
}
```

- Status Code: **401 Unauthorized**
```json
  {
  "message": "Invalid credentials.",
  "loginId": null,
  "nickname": null
}
```

## 2. 전화번호로 아이디 찾기
- **URL**: `/api/users/find-loginId/phone`
- **Method**: `POST`
- **Description**: 전화번호로 `loginId` 조회.

### Request Body
```json
{
  "phoneNumber": "string"
}
```

### Response
- Status Code: **200 OK**
```json
{
  "username": "string"
}

```
- Status Code: **404 Not Found**
- ```json
  {
  "error": "User not found"
}

```

---

## 3. 이메일로 아이디 찾기
- **URL**: `/api/users/find-loginId/email`
- **Method**: `POST`
- **Description**: 이메일로 `loginId` 조회.

### Request Body
```json
{
  "email": "string"
}

```
### Response
- Status Code: **200 OK**
```json
{
  "username": "string"
}

```
- Status Code: **404 Not Found**
- ```json
  {
  "error": "User not found"
}

```

---

## 4. 비밀번호 재설정 요청
- **URL**: `/api/users/password-reset/request`
- **Method**: `POST`
- **Description**: 비밀번호 재설정 요청을 위한 사용자 정보 인증.

### Request Body
```json
{
  "loginId": "string",
  "phoneNumber": "string",
  "email": "string"
}

```
### Response
- Status Code: **200 OK**
```json
"User validated for password reset."
```
- Status Code: **404 Not Found**
- ```json
"User not found or invalid details."
```

---

## 5. 비밀번호 업데이트
- **URL**: `/api/users/password-reset/update`
- **Method**: `POST`
- **Description**: 비밀번호 업데이트.

### Request Body
```json
{
  "loginId": "string",
  "newPassword": "string"
}
```
### Response
- Status Code: **200 OK**
```json
"Password updated successfully."
```
- Status Code: **404 Not Found**
- ```json
"User not found."
```

---

## 6. 로그인 아이디 중복 확인
- **URL**: `/api/users/check-duplicate`
- **Method**: `GET`
- **Description**: 로그인 아이디 중복 확인.

### Query Parameter
- `loginId` (string): 확인할 로그인 아이디.

### Response
- **Status Code**: `200 OK`
  - 중복 시: `"duplicate"`
  - 중복이 아닐 시: `"ok"`

---

## 7. 회원 가입
- **URL**: `/api/users/signup`
- **Method**: `POST`
- **Description**: 회원 가입을 위한 사용자 등록.

### Request Body
```json
{
  "loginId": "string",
  "password": "string",
  "nickname": "string",
  "realName": "string",
  "phoneNumber": "string",
  "email": "string",
  "phoneNotificationConsent": boolean,
  "emailNotificationConsent": boolean
}
```
### Response
- Status Code: **200 OK**
```json
{
  "id": "number",
  "loginId": "string",
  "nickname": "string",
  "realName": "string",
  "phoneNumber": "string",
  "email": "string",
  "phoneNotificationConsent": boolean,
  "emailNotificationConsent": boolean,
  "role": "USER"
}
```
- Status Code: **404 Not Found**
- ```json

```





### Request Body
```json

```
### Response
- Status Code: **200 OK**
```json

```
- Status Code: **404 Not Found**
-
```json

```



### Order API
1. 주문 생성
URL: /order
Method: POST
Request Body:
json
코드 복사
{
  "userId": 1,
  "delivery": {
    "recipientName": "John Doe",
    "phoneNumber": "010-1234-5678",
    "address": "123 Main St",
    "addressDetail": "Apt 101",
    "zipCode": "12345"
  },
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 50000
    }
  ]
}
Response:
json
코드 복사
{
  "orderId": 1,
  "userId": 1,
  "recipientName": "John Doe",
  "phoneNumber": "010-1234-5678",
  "address": "123 Main St",
  "addressDetail": "Apt 101",
  "zipCode": "12345",
  "totalPrice": 100000,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 50000
    }
  ]
}
2. 주문 상세 조회
URL: /order/{orderId}
Method: GET
Response:
json
코드 복사
{
  "orderId": 1,
  "userId": 1,
  "recipientName": "John Doe",
  "phoneNumber": "010-1234-5678",
  "address": "123 Main St",
  "addressDetail": "Apt 101",
  "zipCode": "12345",
  "totalPrice": 100000,
  "shipmentStatus": "PENDING",
  "paymentCompleted": true,
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 50000
    }
  ]
}
3. 주문 결제 완료 및 배송 준비 상태로 업데이트
URL: /order/{orderId}/complete-payment
Method: POST
Query Parameters:
paymentMethod: CARD
amount: 100000
Response:
json
코드 복사
{
  "message": "Order payment completed and shipment created."
}
4. 배송 상태 업데이트
URL: /order/{orderId}/shipment
Method: PUT
Request Body:
json
코드 복사
{
  "status": "SHIPPED",
  "trackingNumber": "123456789",
  "courierCompany": "FedEx"
}
Response:
json
코드 복사
{
  "message": "Shipment status updated successfully."
}
5. 결제 환불 처리
URL: /order/{orderId}/refund
Method: POST
Response:
json
코드 복사
{
  "message": "Payment refunded successfully."
}
6. 주문 목록 조회 (필터링 지원)
URL: /order/user/{userId}
Method: GET
Query Parameters:
shipmentStatus (Optional): DELIVERED
includePayments (Optional): true
Response:
json
코드 복사
[
  {
    "orderId": 1,
    "userId": 1,
    "recipientName": "John Doe",
    "totalPrice": 100000,
    "shipmentStatus": "DELIVERED",
    "paymentCompleted": true
  }
]
7. 특정 주문의 결제 정보 조회
URL: /order/{orderId}/payment
Method: GET
Response:
json
코드 복사
{
  "paymentId": 1,
  "paymentMethod": "CARD",
  "amount": 100000,
  "paymentDate": "2024-11-29",
  "isSuccessful": true
}
8. 특정 주문의 배송 정보 조회
URL: /order/{orderId}/shipment
Method: GET
Response:
json
코드 복사
{
  "shipmentId": 1,
  "trackingNumber": "123456789",
  "courierCompany": "FedEx",
  "shipmentStatus": "SHIPPED",
  "shippedAt": "2024-11-29",
  "deliveredAt": null
}
오늘 나눈 대화 요약



























