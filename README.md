Fream 리빌딩 프로젝트
<br>
기존의 Kream클론 프로젝트에서 부족한 점을 개선하고 리빌딩하기 위한 프로젝트
<br>
기존 프로젝트 : https://github.com/junhyeong9812/fream_back
<br>
엔티티 설계 과정 : https://unleashed-moon-059.notion.site/Kream-15a256e3f89b80438417cdd16f30f3d0
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

  - 주문,결제내역,쉽먼트,스타일 엔티티 구현
  - 주문 생성
  - 주문 상세 조회
  - 주문 결제 완료 및 배송 준비 상태로 업데이트
  - 배송 상태 업데이트
  - 결제 환불 처리
  - 주문 목록 조회 (필터링 지원)
  - 주문 결제 및 배송 정보 조회
  - 특정 주문의 결제 정보 조회
  - 특정 주문의 배송 정보 조회
  - style 구현을 통한 짧은 영상 및 사진 공유 api 구현(완)
  - portone연동을 통한 실제 구매 및 판매(완)
  - 오더엔티티 portone api에 맞도록 재구성(완)
  - 
- 2차 프로렉트 미완성 부분 정리 :
  - 사이트 접속 통계 수집 api 구현(미완)
 
- 3차 프로젝트 :24년 12월 9일~24년 12월 13일
- 프론트 엔드 프로젝트 시작 : 24년 12월 9일
- 3차 프로젝트 :
  - portone연동을 통한 실제 구매 및 판매(완)
  - 오더엔티티 portone api에 맞도록 재구성(완)
  - User엔티티 재설계
### 재설계 이유
    ```
    기존의 흐름을 단순히 머리로 생각한 부분을 고려해서 구현해보았지만 실제 Kream사이트를 보고 
    프론트엔드 개발을 하며 프론트 구조를 뜯어보고 확인하면서 각 엔티티별 기능 및 필요한 기능에 대한 재정립을 해보았을 때
    기존의 구조는 Kream의 구조가 아닌 단순히 e커머스의 기본 구조밖에 안되며 
    위에서 1차 2차 개발을 통해 만든 내용 자체가 하나하나 확인하며 체크했을 때 너무도 다르다는 것을 확인했고 
    실제로 API요청을 통한 동작은 전부 테스트 해보았지만 위 구조는 현재 하려는 Kream사이트의 구조와 다르고 각각의 연관관계 및 
    로직의 설계가 동작만하고 미흡한 부분이 많은 것을 확인 
    하나의 서비스 구조에 모든 구조를 다 넣어놔서 코드 파악도 힘들 뿐더러 기능별 분리가 제대로 되어 있지 않았다.
    그래서 다시 차근차근 프론트엔드를 만들며 
    https://unleashed-moon-059.notion.site/Kream-15a256e3f89b80438417cdd16f30f3d0
    여기에 구조들을 뜯어보며 백엔드 기능을 다시 재구성하고 다시 하나하나씩 만들었을 때 
    기존에 1차 2차에서 만들었던 기능을 토대로 다시 만드는 부분이기에 확실히 시스템적으로 흐름도 안정적이고 코드도 기능별로 기존보다  
    훨씬 잘 나눠져서 유지보수 하기도 편해져서 전체 구조를 다시 재설계하려고 한다.
    ```
    
 
- 3차 프로젝트 미완성
  - 사이트 접속 통계 수집 api 구현(미완)
  - 도커 및 마이크로서비스 아키텍처 도입(미완)
  - AWS 배포(미완)
  - 추가적인 성능 최적화(미완)
  - Elasticsearch 도입해보기(미완)
  - 대규모 트래픽 처리를 위한 로드 밸런싱 및 캐싱 전략 도입(미완)

-  4차 프로젝트 :24년 12월 16일~24년 12월 20일
-  4차 프로젝트 예정 
  - 알림 엔티티 설계 (완)
  - 웹 소캣을 활용한 알림 기능 구현 (완)
  - 공지사항 관련 엔티티 및 기능 구현 (완)
  - product 구조 재설계(미완)
  - order 구조 재설계(미완)
  - style 구조 재설계(미완)





<br>
기본 ERD구조
<br>
<img src="https://github.com/user-attachments/assets/c040190e-b830-451d-a873-f0c0640dc05f"/>
<br>



# API 명세서
### 1. 사용자(User) API
## 1.1 로그인
- **URL**: /api/users/login
- **Method**: POST
- **Description**: 사용자의 loginId와 password로 로그인. 성공 시 JWT 토큰 발급 및 사용자 정보를 반환.
### Request Body
```json
{
  "loginId": "string",
  "password": "string"
}
```
### Response (200 OK)
```json
{
  "message": "Login successful.",
  "loginId": "string",
  "nickname": "string",
  "token": null
}
```
### Response (401 Unauthorized)
```json
{
  "message": "Invalid credentials.",
  "loginId": null,
  "nickname": null,
  "token": null
}
```
## 1.2 로그아웃
- **URL**: /api/users/logout
- **Method**: POST
- **Description**: 현재 사용 중인 JWT 토큰을 무효화.
Headers
Authorization: Bearer <token>
Response (200 OK)
```text
Logout successful.
```
Response (400 Bad Request)
``` text
Invalid token.
```
## 1.3 아이디 찾기 (전화번호)
- **URLL**: /api/users/find-loginId/phone
- **MethodL**: POST
- **DescriptionL**: 사용자의 전화번호로 loginId 조회.
### Request Body
```json
{
  "phoneNumber": "string"
}
```
Response (200 OK)
```json
{
  "loginId": "string"
}
```
Response (404 Not Found)
```json
null
```
## 1.4 아이디 찾기 (이메일)
- **URLL**: /api/users/find-loginId/email
- **MethodL**: POST
- **DescriptionL**: 사용자의 이메일로 loginId 조회.
### Request Body
```json
{
  "email": "string"
}
```
Response (200 OK)
```json
{
  "loginId": "string"
}
```
Response (404 Not Found)
``` json
null
```
## 1.5 비밀번호 재설정 요청
- **URLL**: /api/users/password-reset/request
- **MethodL**: POST
- **DescriptionL**: 비밀번호 재설정을 위한 사용자 검증 요청.
### Request Body
```json
{
  "loginId": "string",
  "phoneNumber": "string",
  "email": "string"
}
```
Response (200 OK)
```text
User validated for password reset.
```
Response (404 Not Found)
``` text
User not found or invalid details.
```
## 1.6 비밀번호 업데이트
- **URLL**: /api/users/password-reset/update
- **MethodL**: POST
- **DescriptionL**: 사용자 비밀번호 변경.
### Request Body
```json
{
  "loginId": "string",
  "newPassword": "string"
}
```
Response (200 OK)
``` text
Password updated successfully.
```
Response (404 Not Found)
``` text
User not found.
```
## 1.7 로그인 아이디 중복 확인
- **URLL**: /api/users/check-duplicate
- **MethodL**: GET
- **DescriptionL**: 로그인 ID의 중복 여부 확인.
Query Parameters
loginId: string
Response (200 OK)
```text
"ok": 중복되지 않음.
"duplicate": 중복됨.
```
## 1.8 회원가입
- **URLL**: /api/users/signup
- **MethodL**: POST
- **DescriptionL**: 새로운 사용자 등록.
### Request Body
```json
{
  "loginId": "string",
  "password": "string",
  "nickname": "string",
  "realName": "string",
  "phoneNumber": "string",
  "email": "string",
  "phoneNotificationConsent": false,
  "emailNotificationConsent": false
}
```
Response (200 OK)
```json
{
  "id": 1,
  "loginId": "string",
  "nickname": "string",
  "realName": "string",
  "phoneNumber": "string",
  "email": "string",
  "role": "USER"
}
```
### 2. 배송지(Delivery) API
## 2.1 배송지 추가
- **URLL**: /api/deliveries/add
- **MethodL**: POST
- **DescriptionL**: 배송지 추가 요청.
### Request Body
```json
{
  "recipientName": "string",
  "phoneNumber": "string",
  "address": "string",
  "addressDetail": "string",
  "zipCode": "string",
  "isDefault": true
}
```
Response (200 OK)
``` text
배송지가 성공적으로 추가되었습니다.
```
## 2.2 배송지 목록 조회
- **URLL**: /api/deliveries/list
- **MethodL**: GET
- **DescriptionL**: 특정 사용자의 모든 배송지 목록 조회.
Query Parameters
loginId: string
Response (200 OK)
```json
[
  {
    "id": 1,
    "recipientName": "string",
    "phoneNumber": "string",
    "address": "string",
    "addressDetail": "string",
    "zipCode": "string",
    "isDefault": true
  }
]
```
## 2.3 배송지 수정
- **URLL**: /api/deliveries/update
- **MethodL**: PUT
- **DescriptionL**: 배송지 수정 요청.
### Request Body
```json
{
  "id": 1,
  "recipientName": "string",
  "phoneNumber": "string",
  "address": "string",
  "addressDetail": "string",
  "zipCode": "string",
  "isDefault": true
}
```
Response (200 OK)
``` text
배송지 정보가 성공적으로 수정되었습니다.
```
## 2.4 배송지 삭제
- **URLL**: /api/deliveries/delete
- **MethodL**: DELETE
- **DescriptionL**: 특정 배송지 삭제.
### Request Body
```json
{
  "id": 1
}
```
Response (200 OK)
``` text
배송지가 성공적으로 삭제되었습니다.
```
### 3. 상품(Product) API
## 3.1 임시 URL 생성
- **URLL**: /api/products/temporary-url
- **MethodL**: POST
- **DescriptionL**: 이미지 업로드 후 임시 URL 생성.
Request Parameters
file: <image_file>
Response (200 OK)
```text

temp/<file_path>
```
3.2 상품 생성
URLL**: /api/products
MethodL**: POST
DescriptionL**: 새로운 상품 등록.
### Request Body
```json
{
  "name": "string",
  "brand": "string",
  "mainCategoryId": 1,
  "subCategoryId": 2,
  "initialPrice": 100.00,
  "description": "string",
  "releaseDate": "2024-12-01",
  "images": [
    {
      "imageName": "string",
      "temp_Url": "string",
      "imageType": "string",
      "isMainThumbnail": true
    }
  ],
  "sizeAndColorQuantities": [
    {
      "sizeType": "string",
      "clothingSizes": ["M", "L"],
      "colors": ["RED", "BLUE"],
      "quantity": 15
    }
  ]
}
```
Response (200 OK)
```json
{
  "id": 1
}
```
## 3.3 상품 수정
- **URLL**: /api/products/{productId}
- **MethodL**: PUT
- **DescriptionL**: 상품 정보 수정.
### Request Body
```json
{
  "name": "string",
  "brand": "string",
  "mainCategoryId": 1,
  "subCategoryId": 2,
  "initialPrice": 100.00,
  "description": "string",
  "releaseDate": "2024-12-01",
  "images": [...],
  "sizeAndColorQuantities": [...]
}
```
Response (200 OK)
```json
{
  "id": 1
}
```
## 3.4 상품 삭제
- **URLL**: /api/products/{productId}
- **MethodL**: DELETE
- **DescriptionL**: 상품 삭제 요청.
Response (200 OK)
text
코드 복사
상품이 성공적으로 삭제되었습니다.
## 3.5 단일 상품 조회
URLL**: /api/products/{productId}
MethodL**: GET
DescriptionL**: 상품 상세 조회.
Response (200 OK)
  ```json
{
  "id": 1,
  "name": "string",
  "brand": "string",
  "description": "string",
  "images": [...],
  "sizeAndColorQuantities": [...]
}
```
## 3.6 필터링된 상품 조회
- **URLL**: /api/products/filter
- **MethodL**: GET
- **DescriptionL**: 필터 조건에 따른 상품 목록 조회.
Query Parameters
mainCategoryId
subCategoryId
color
size
brand
sortBy
Response (200 OK)
```json
[
  {
    "id": 1,
    "name": "string",
    "brand": "string",
    "mainCategoryName": "string",
    "subCategoryName": "string",
    "colors": ["RED", "BLUE"],
    "sizes": ["M", "L"],
    "quantity": 15
  }
]
```

### 4. 주문(Order) API
## 4.1 주문 생성
- **URLL**: /order
- **MethodL**: POST
- **DescriptionL**: 사용자가 주문을 생성합니다.
### Request Body
```json
{
  "userId": 1,
  "deliveryId": 2,
  "orderItems": [
    {
      "productId": 10,
      "quantity": 2,
      "price": 100.00
    }
  ],
  "delivery": {
    "recipientName": "John Doe",
    "phoneNumber": "010-1234-5678",
    "address": "123 Test St",
    "addressDetail": "Apt 101",
    "zipCode": "12345"
  },
  "payment": {
    "paymentMethod": "Credit Card",
    "amount": 200.00
  }
}
```
### Response (200 OK)
```json
{
  "orderId": 1,
  "userId": 1,
  "orderItems": [...],
  "shipmentStatus": "Ready",
  "trackingNumber": null,
  "courierCompany": null,
  "totalPrice": 200.00
}
```
## 4.2 주문 상세 조회
- **URLL**: /order/{orderId}
- **MethodL**: GET
- **DescriptionL**: 특정 주문의 상세 정보를 조회합니다.
### Response (200 OK)
```json
{
  "orderId": 1,
  "userId": 1,
  "orderItems": [...],
  "shipmentStatus": "Shipped",
  "trackingNumber": "123456",
  "courierCompany": "FedEx",
  "totalPrice": 200.00
}
```
## 4.3 결제 완료 및 배송 준비 상태 업데이트
- **URLL**: /order/{orderId}/complete-payment
- **MethodL**: POST
- **DescriptionL**: 주문 결제를 완료하고 배송 준비 상태로 업데이트합니다.
### Request Parameters
```makefile
paymentMethod=Credit Card
amount=200.00
```
Response (200 OK)
```text
Order payment completed and shipment created.
```
## 4.4 배송 상태 업데이트
- **URLL**: /order/{orderId}/shipment
- **MethodL**: PUT
- **DescriptionL**: 특정 주문의 배송 상태를 업데이트합니다.
### Request Body
```json
{
  "shipmentStatus": "Delivered",
  "trackingNumber": "123456",
  "courierCompany": "FedEx"
}
```
### Response (200 OK)
lua
```text
Shipment status updated successfully.
```
## 4.5 결제 환불 처리
- **URLL**: /order/{orderId}/refund
- **MethodL**: POST
- **DescriptionL**: 특정 주문의 결제를 환불합니다.
### Response (200 OK)
```
Payment refunded successfully.
```
## 4.6 주문 목록 조회
- **URL: /order/user/{userId}
- **Method: GET
- **Description: 특정 사용자의 주문 목록을 조회합니다.
### Request Parameters
makefile
```
shipmentStatus=Shipped
includePayments=true
```
### Response (200 OK)
```json
[
  {
    "orderId": 1,
    "userId": 1,
    "orderItems": [...],
    "shipmentStatus": "Shipped",
    "totalPrice": 200.00
  },
  ...
]
```
### 5. 스타일(Style) API
## 5.1 임시 저장
- **URLL**: /styles/upload-temp
- **MethodL**: POST
- **DescriptionL**: 스타일 관련 파일을 임시로 저장합니다.
### Request Parameters
makefile
```
file=<MultipartFile>
```
### Response (200 OK)
```json
temp/1234567890_dummy-image.jpg
```
## 5.2 스타일 생성
- **URLL**: /styles/create
- **MethodL**: POST
- **DescriptionL**: 새로운 스타일을 생성합니다.
### Request Body
```json
{
  "userId": 1,
  "orderItemId": 10,
  "content": "Amazing product!",
  "rating": 5,
  "tempFilePath": "temp/1234567890_dummy-image.jpg"
}
```
### Response (200 OK)
```
1
```
## 5.3 스타일 수정
- **URLL**: /styles/{styleId}/update
- **MethodL**: PUT
- **DescriptionL**: 특정 스타일을 수정합니다.
### Request Body
```json
{
  "userId": 1,
  "content": "Updated content!",
  "rating": 4,
  "tempFilePath": "temp/updated-image.jpg"
}
```
### Response (200 OK)
```
1
```
## 5.4 스타일 삭제
- **URLL**: /styles/{styleId}/delete
- **MethodL**: DELETE
- **DescriptionL**: 특정 스타일을 삭제합니다.
### Request Parameters
makefile
```
userId=1
```
### Response (200 OK)
```
스타일이 성공적으로 삭제되었습니다.
```
## 5.5 스타일 검색
- **URLL**: /styles/search
- **MethodL**: POST
- **DescriptionL**: 특정 조건에 맞는 스타일을 검색합니다.
### Request Body
```json
{
  "userId": 1,
  "productId": 10,
  "keyword": "cool"
}
```
### Response (200 OK)
```json
[
  {
    "id": 1,
    "content": "Amazing style!",
    "rating": 5,
    "imageUrl": "images/style_1.jpg",
    "createdDate": "2024-12-07T22:10:00"
  },
  ...
]
```
## 5.6 스타일 상세 조회
- **URLL**: /styles/{styleId}
- **MethodL**: GET
- **DescriptionL**: 특정 스타일의 상세 정보를 조회합니다.
### Response (200 OK)
```json
{
  "id": 1,
  "content": "Amazing style!",
  "rating": 5,
  "imageUrl": "images/style_1.jpg",
  "userNickname": "John Doe",
  "productId": 10,
  "productName": "Cool Shoes",
  "productBrand": "Nike",
  "productImageUrl": "images/product_10.jpg"
}
```




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
```json
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
```
7. 특정 주문의 결제 정보 조회
URL: /order/{orderId}/payment
Method: GET
Response:
```json
{
  "paymentId": 1,
  "paymentMethod": "CARD",
  "amount": 100000,
  "paymentDate": "2024-11-29",
  "isSuccessful": true
}
```
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



























