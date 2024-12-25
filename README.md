Fream 리빌딩 프로젝트
<br>
기존의 Kream클론 프로젝트에서 부족한 점을 개선하고 리빌딩하기 위한 프로젝트
<br>
기존 프로젝트 : https://github.com/junhyeong9812/fream_back
<br>
엔티티 설계 과정 : https://unleashed-moon-059.notion.site/Kream-15a256e3f89b80438417cdd16f30f3d0
<br>
PostMan API경로 : https://web.postman.co/workspace/5b556cbc-5c81-44b7-8ebd-85f4023ecae3/request/38241845-816ab6bf-2756-4868-8860-f03fe7544a49?tab=body
<br>

### 개요
```
이번 프로젝트는 기존에 진행했던 KREAM 리셀 사이트 클론 프로젝트에서의 경험과 아쉬움을 바탕으로, 처음부터 다시 설계하고 구현하는 **리셀 플랫폼 리빌딩 프로젝트**입니다. KREAM은 e-Commerce의 기본적인 요소와 커뮤니티 기능(스타일 공유, 댓글, 추천 등)을 모두 갖춘 플랫폼으로, 웹 개발에 필요한 주요 기술을 종합적으로 익히기에 적합한 프로젝트입니다.

이번 프로젝트는 다음과 같은 기술 스택을 활용하여 개발 중입니다:

1. **백엔드**: Spring Boot, JPA, H2 Database
2. **프론트엔드**: React.js, TypeScript
3. **데이터 관리**: DTO를 활용한 데이터 독립성 확보, QueryDSL을 통한 효율적 조회
4. **구조 설계**: 도메인 주도 설계(DDD)를 통해 유지보수성과 확장성을 고려한 모듈화
```

### 기획의도
```
기존 프로젝트에서 발견했던 한계점을 해결하고, 더 깊이 있는 학습을 통해 실무 수준의 결과물을 만들어내고자 이번 프로젝트를 시작했습니다.

**기존 한계점**

- **설계와 구조의 한계**
    - API가 엔티티에 종속적이어서 확장성과 유지보수성이 부족했습니다.
    - 이를 개선하기 위해 DTO를 적극 활용하고, API 설계를 데이터 흐름 중심으로 개선했습니다.
- **기능적 부족**
    - 데이터 조회 성능 최적화 미흡(N+1 문제 등)
    - 이번 프로젝트에서는 QueryDSL과 JPA를 통해 효율적인 데이터 처리 및 조회 구조를 설계했습니다.
- **실무적 배포 경험 부족**
    - 클라우드와 컨테이너 기반 환경에서의 운영 경험이 부족했습니다.
    - 이번 프로젝트에서는 AWS 배포 및 Docker를 활용한 컨테이너화까지 학습 및 적용할 예정입니다.
```

### 프로젝트 목표
- **e-Commerce와 커뮤니티 기능의 통합 플랫폼 구현**
- **유지보수성과 확장성을 고려한 모듈화된 백엔드 설계**
- **실무에서 요구되는 성능 최적화 및 보안 강화 기술 적용**

재설계 이전 :https://unleashed-moon-059.notion.site/160256e3f89b809ab490c949fd5c3875
## 구현 이후 재설계를 한 이유
```
기존의 흐름을 단순히 머리로 생각한 부분을 고려해서 구현해보았지만 실제 Kream사이트를 보고
프론트엔드 개발을 하며 프론트 구조를 뜯어보고 확인하면서 각 엔티티별 기능 및 필요한 기능에 대한 재정립을 해보았을 때
기존의 구조는 Kream의 구조가 아닌 단순히 e커머스의 기본 구조밖에 안되며
위에서 1차 2차 개발을 통해 만든 내용 자체가 하나하나 확인하며 체크했을 때 너무도 다르다는 것을 확인했고
실제로 API요청을 통한 동작은 전부 테스트 해보았지만 위 구조는 현재 하려는 Kream사이트의 구조와 다르고 각각의 연관관계 및
로직의 설계가 동작만하고 미흡한 부분이 많은 것을 확인
하나의 서비스 구조에 모든 구조를 다 넣어놔서 코드 파악도 힘들 뿐더러 기능별 분리가 제대로 되어 있지 않았다.
그래서 다시 차근차근 프론트엔드를 만들며
<https://unleashed-moon-059.notion.site/Kream-15a256e3f89b80438417cdd16f30f3d0>
여기에 구조들을 뜯어보며 백엔드 기능을 다시 재구성하고 다시 하나하나씩 만들었을 때
기존에 1차 2차에서 만들었던 기능을 토대로 다시 만드는 부분이기에 확실히 시스템적으로 흐름도 안정적이고 코드도 기능별로 기존보다
훨씬 잘 나눠져서 유지보수 하기도 편해져서 전체 구조를 다시 재설계하려고 한다.
```
# 설계 개요

- **도메인 중심 설계**
    - **User, Product, Order, Style 등의 도메인을 중심으로 모듈화된 아키텍처 설계**
- **데이터 최적화**
    - **QueryDSL과 페이징 처리로 대량 데이터 조회 성능 개선**
    - **Lazy Loading 및 Fetch Join 전략으로 N+1 문제 해결**
- **보안 강화**
    - **JWT와 Spring Security를 활용한 인증 및 권한 관리**
    - **Redis를 이용한 토큰 화이트리스트 관리**
- **배포 및 운영**
    - **AWS와 Docker를 통한 안정적인 배포 환경 구축**
    - **향후 Kubernetes를 통해 마이크로서비스 아키텍처 적용 예정**


## 재설계 1차 프로젝트 : 12월 9일~ 12월 13일
  - **사용자 도메인**
    ![image](https://github.com/user-attachments/assets/8b089d5f-132f-467b-8339-8b7d88df610c)

# 사용자 관련 기능 및 API 명세서
## 1.1 로그인
- **URL: /api/users/login**
- **Method: POST**
- **Description: 사용자의 email과 password로 로그인. 성공 시 JWT 토큰 발급.**
### Request Body
```
{
  "email": "string",
  "password": "string"
}
```

### Response (200 OK)
```
{
  "token": "string"
}
```
<br>
### Response (401 Unauthorized)
```
{
  "status": "error",
  "message": "Invalid credentials."
}
```
## 1.2 회원가입
- **URL: /api/users/register**
- **Method: POST**
- **Description: 회원 정보를 받아 사용자 등록.**

### Request Body
```
{
  "email": "string",
  "password": "string",
  "phoneNumber": "string",
  "referralCode": "string",
  "shoeSize": "string",
  "isOver14": true,
  "termsAgreement": true,
  "privacyAgreement": true,
  "optionalPrivacyAgreement": true,
  "adConsent": true
}
```
### Response (201 Created)
```
{
  "status": "success",
  "userEmail": "string"
}
```
### Response (400 Bad Request)
```
{
  "status": "error",
  "message": "Invalid input."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "회원가입 처리 중 문제가 발생했습니다."
}
```
## 1.3 이메일 찾기
- **URL: /api/users/find-email**
- **Method: POST**
- **Description: 사용자의 전화번호로 이메일을 조회.**
### Request Body
```
{
  "phoneNumber": "string"
}
```
### Response (200 OK)
```
{
  "email": "string"
}
```
### Response (404 Not Found)
```
{
  "status": "error",
  "message": "이메일을 찾을 수 없습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "이메일 찾기 처리 중 문제가 발생했습니다."
}
```
### 1.4 비밀번호 초기화 가능 여부 확인
- **URL: /api/users/reset-password**
- **Method: POST**
- **Description: 이메일과 전화번호를 통해 비밀번호 초기화 가능 여부 확인.**
### Request Body
```
{
  "email": "string",
  "phoneNumber": "string"
}
```
### Response (200 OK)
```
{
  "status": "ok"
}
```
### Response (404 Not Found)
```
{
  "status": "error",
  "message": "해당 이메일 및 전화번호로 사용자를 찾을 수 없습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "비밀번호 찾기 처리 중 문제가 발생했습니다."
}
```
### 1.5 비밀번호 변경
- **URL: /api/users/reset**
- **Method: POST**
- **Description: 새로운 비밀번호를 설정.**
### Request Body
```
{
  "email": "string",
  "phoneNumber": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```
### Response (200 OK)
```
"비밀번호가 성공적으로 변경되었습니다."
```
### Response (400 Bad Request)
```
"비밀번호와 비밀번호 확인이 일치하지 않습니다."
```
### Response (500 Internal Server Error)
```
"비밀번호 변경 중 문제가 발생했습니다."
```

## 1.6 로그인 정보 변경
- **URL: /api/users/update-login-info**
- **Method: PUT**
- **Description: 사용자의 로그인 정보를 업데이트.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Request Body
```
{
  "newEmail": "string",
  "password": "string",
  "newPassword": "string",
  "newPhoneNumber": "string",
  "newShoeSize": "string",
  "adConsent": true,
  "privacyConsent": true,
  "smsConsent": true,
  "emailConsent": true
}
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "로그인 정보가 성공적으로 변경되었습니다."
}
```
### Response (400 Bad Request)
```
{
  "status": "error",
  "message": "잘못된 입력 값입니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "로그인 정보 변경 처리 중 문제가 발생했습니다."
}
```
## 1.7 로그인 정보 조회
- **URL: /api/users/login-info**
- **Method: GET**
- **Description: 사용자의 로그인 정보를 조회.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Response (200 OK)
```
{
  "email": "string",
  "phoneNumber": "string",
  "shoeSize": "string",
  "optionalPrivacyAgreement": true,
  "smsConsent": true,
  "emailConsent": true
}
```
### Response (404 Not Found)
```
null
```
### Response (500 Internal Server Error)
```
null
```
## 1.8 회원 탈퇴
- **URL: /api/users/delete-account**
- **Method: DELETE**
- **Description: 사용자의 계정을 삭제.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "회원 탈퇴가 완료되었습니다."
}
```
### Response (404 Not Found)
```
{
  "status": "error",
  "message": "사용자를 찾을 수 없습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "회원 탈퇴 처리 중 문제가 발생했습니다."
}
```
## 1.9 비밀번호 찾기 - 이메일로 임시 비밀번호 전송
- **URL: /api/users/reset-password-sandEmail**
- **Method: POST**
- **Description: 사용자의 이메일로 임시 비밀번호를 전송합니다. 임시 비밀번호는 랜덤으로 생성되며, 이메일 전송 후 암호화되어 저장됩니다.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Request Body
```
{
  "email": "string",
  "phoneNumber": "string"
}
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "임시 비밀번호가 이메일로 전송되었습니다."
}
```
### Response (404 Not Found)
```
{
  "status": "error",
  "message": "해당 이메일 및 전화번호로 사용자를 찾을 수 없습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "비밀번호 찾기 처리 중 문제가 발생했습니다."
}
```



## 2.1 프로필 조회
- **URL: /api/profiles**
- **Method: GET**
- **Description: 사용자의 프로필 정보를 조회.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Response (200 OK)
```
{
  "profileImage": "string",
  "profileName": "string",
  "realName": "string",
  "bio": "string",
  "isPublic": true,
  "blockedProfiles": [
    {
      "profileId": "number",
      "profileName": "string",
      "profileImageUrl": "string"
    }
  ]
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "프로필 정보를 불러오는 중 문제가 발생했습니다."
}
```
## 2.2 프로필 업데이트
-**URL: /api/profiles**
-**Method: PUT**
-** Description: 사용자의 프로필 정보를 업데이트. 이미지 파일은 Multipart 형식으로 받음.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Request Body (Multipart Form Data)
```
{
  "profileImage": "file (binary)",
  "dto": {
    "profileName": "string",
    "realName": "string",
    "bio": "string",
    "isPublic": "boolean"
  }
}
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "프로필이 성공적으로 업데이트되었습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "프로필 업데이트 중 문제가 발생했습니다."
}
```
## 2.3 프로필 이미지 제공
- **URL: /api/profiles/{profileId}/image**
- **Method: GET**
- **Description: 특정 프로필의 프로필 이미지를 제공.**
## Path Parameters
```
profileId: 프로필 ID
```
### Response (200 OK)
```
Content-Type: 이미지 파일 형식 (e.g., image/jpeg)
```
### Response (404 Not Found)
```
{
  "status": "error",
  "message": "이미지 파일이 존재하지 않습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "프로필 이미지를 불러오는 중 문제가 발생했습니다."
}
```

## 3.1 팔로우 생성
- **URL: /api/follows/{profileId}**
- **Method: POST**
- **Description: 특정 프로필을 팔로우.

### Request Header
```
Authorization: Bearer <JWT Token>
```
### Path Parameters
```
profileId: 팔로우할 프로필 ID
```
### Response (200 OK)
```
"팔로우가 성공적으로 추가되었습니다."
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "팔로우 생성 중 문제가 발생했습니다."
}
```
## 3.2 팔로우 삭제
- **URL: /api/follows/{profileId}**
- **Method: DELETE**
- **Description: 특정 프로필 팔로우를 취소.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Path Parameters
```
profileId: 취소할 팔로우 프로필 ID
```
### Response (200 OK)
```
"팔로우가 성공적으로 삭제되었습니다."
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "팔로우 삭제 중 문제가 발생했습니다."
}
```
## 3.3 팔로워 목록 조회
- **URL: /api/follows/followers**
- **Method: GET**
- **Description: 로그인 사용자의 팔로워 목록을 조회.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Query Parameters
```
page: 페이지 번호 (default: 0)

size: 페이지 크기 (default: 20)
```
### Response (200 OK)
```
{
  "content": [
    {
      "profileId": "number",
      "profileName": "string",
      "profileImageUrl": "string"
    }
  ],
  "pageable": {
    "page": "number",
    "size": "number"
  }
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "팔로워 목록 조회 중 문제가 발생했습니다."
}
```
## 3.4 팔로잉 목록 조회
- **URL: /api/follows/followings**
- **Method: GET**
- **Description: 로그인 사용자의 팔로잉 목록을 조회.**

### Request Header
```
Authorization: Bearer <JWT Token>
```
### Query Parameters
```
page: 페이지 번호 (default: 0)

size: 페이지 크기 (default: 20)
```
### Response (200 OK)
```
{
  "content": [
    {
      "profileId": "number",
      "profileName": "string",
      "profileImageUrl": "string"
    }
  ],
  "pageable": {
    "page": "number",
    "size": "number"
  }
}
```
### Response (500 Internal Server Error)


## 4.1 프로필 차단
- **URL: /api/profiles/blocked**
- **Method: POST**
- **Description: 특정 프로필을 차단.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Request Parameters
```
blockedProfileId: (Long) 차단할 프로필의 ID
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "프로필 차단이 완료되었습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "프로필 차단 중 문제가 발생했습니다."
}
```
## 4.2 프로필 차단 해제
- **URL: /api/profiles/blocked**
- **Method: DELETE**
- **Description: 특정 프로필에 대한 차단을 해제.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Request Parameters
```
blockedProfileId: (Long) 차단 해제할 프로필의 ID
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "프로필 차단이 해제되었습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "프로필 차단 해제 중 문제가 발생했습니다."
}
```
## 4.3 차단된 프로필 목록 조회
- **URL: /api/profiles/blocked**
- **Method: GET**
- **Description: 사용자가 차단한 프로필 목록을 조회.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Response (200 OK)
```
[
  {
    "profileId": "number",
    "profileName": "string",
    "profileImageUrl": "string"
  }
]
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "차단된 프로필 목록 조회 중 문제가 발생했습니다."
}
```
## 5.1 입금 계좌 정보 생성 및 수정
- **URL: /api/bank-account**
- **Method: POST**
- **Description: 판매 정산 계좌 정보를 등록하거나 수정.**

### Request Header
```
Authorization: Bearer <JWT Token>
```
### Request Body
```
{
  "bankName": "string",
  "accountNumber": "string",
  "accountHolder": "string"
}
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "판매 정산 계좌가 성공적으로 등록/수정되었습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "입금 계좌 정보 등록/수정 중 문제가 발생했습니다."
}
```
## 5.2 입금 계좌 정보 조회
- **URL: /api/bank-account**
- **Method: GET**
- **Description: 사용자의 판매 정산 계좌 정보를 조회.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Response (200 OK)
```
{
  "bankName": "string",
  "accountNumber": "string",
  "accountHolder": "string"
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "입금 계좌 정보 조회 중 문제가 발생했습니다."
}
```
## 5.3 입금 계좌 정보 삭제
- **URL: /api/bank-account**
- **Method: DELETE**
- **Description: 사용자의 판매 정산 계좌 정보를 삭제.**
### Request Header
```
Authorization: Bearer <JWT Token>
```
### Response (200 OK)
```
{
  "status": "success",
  "message": "판매 정산 계좌가 성공적으로 삭제되었습니다."
}
```
### Response (500 Internal Server Error)
```
{
  "status": "error",
  "message": "입금 계좌 정보 삭제 중 문제가 발생했습니다."
}
```






  - 고객센터 도메인

  - 웹 소캣을 활용한 알림 기능 구현


# 환경 설정
이 프로젝트는 민감한 정보를 분리 관리하기 위해 **application.yml**과 application-<profile>.yml 파일을 사용합니다. 로컬 개발 환경에서는 기본적으로 application-local.yml을 사용하도록 설정되어 있습니다.

## 1 필수 설정 파일
1. application.yml
- 기본 설정을 포함하며, Git에 포함되어 있습니다.
- 프로파일에 따라 추가 설정 파일(application-local.yml, application-prod.yml)을 로드합니다.
2. application-local.yml
- 로컬 개발 환경에서 필요한 민감한 정보를 포함합니다.
- 이 파일은 Git에 포함되지 않으며, 다음 경로에 생성해야 합니다:
```
src/main/resources/application-local.yml
```
##2 application-local.yml 샘플
```
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: myuser
    password: mypassword
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-email-password
jwt:
  secret: your-secret-key
  expiration: 3600000      # JWT 만료 시간 (밀리초 단위, 예: 1시간)
portone:
  api-key: ${PORTONE_API_KEY} # 포트원의 API 키
  api-secret: ${PORTONE_API_SECRET} # 포트원의 Secret 키

```
































