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
- ```json

```




























