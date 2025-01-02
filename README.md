# Fream 리빌딩 프로젝트

## 1. 프로젝트 개요
- 기존 Kream 클론 프로젝트에서 부족했던 점을 개선하고, e-Commerce와 커뮤니티 기능을 통합한 리셀 플랫폼을 다시 설계/구현한 프로젝트입니다.

## 2. 주요 기능
- **유저 계정**: 회원가입, 로그인, 프로필, 팔로우/차단
- **상품 관리**: 등록, 수정, 삭제, 이미지 관리
- **구매/판매/결제/배송**: 실제 리셀 프로세스 구현
- **스타일 커뮤니티**: 스타일 등록/좋아요/댓글
- **알림/공지/FAQ**: 실시간 알림(WebSocket), 공지, FAQ 관리

## 3. 기술 스택
- **Backend**: Spring Boot, JPA, QueryDSL, Spring Security, Redis
- **DB**: H2(개발), MySQL/AWS RDS(운영)
- **Infra**: AWS EC2, S3, Docker 등

## 4. 환경 요구사항 (Prerequisites)

1. **Java 11+**
   - 설치 확인: `java -version`
2. **Docker & Docker Compose**
   - Redis 구동을 위해 필요
3. **Git**
   - 저장소 클론을 위해 필요
  
> **참고**: 이 프로젝트에는 Gradle Wrapper(`gradlew`, `gradlew.bat`)가 포함되어 있으므로,  
> 별도의 Gradle 설치 없이도 `./gradlew` 명령을 통해 빌드하고 실행할 수 있습니다.

## 5. 설치 및 실행

### 5.1 프로젝트 클론
```bash
git clone https://github.com/your-repo/Improve_Fream_Back.git
cd Improve_Fream_Back
```

### 5-2.Docker로 Redis 실행
1. Docker & Docker Compose 설치 확인
    - docker -v
    - docker-compose -v
    - 위 명령어로 버전이 표시되면 설치가 잘 된 것입니다.
2. docker-compose.yml 파일 확인
    - docker-compose up -d 명령어로 Redis 컨테이너를 백그라운드로 실행시킵니다.
3. Redis 실행 확인
    - docker ps → redis_server(또는 redis 컨테이너 이름)이 실행 중인지 확인
    - docker logs redis_server로 에러 로그 여부 확인
  
### 5.3 로컬 환경설정 (application-local.yml)
    - 이 프로젝트는 민감 정보를 분리 관리하기 위해 application-local.yml 파일을 사용합니다.
    -아래와 같이 H2 인메모리 DB 및 Redis 설정이 포함된 샘플을 참고하여, 개인 키/패스워드를 직접 작성해주세요:
```
src/main/resources/application-local.yml
```
    - application-local.yml 샘플
```
spring:
  datasource:
    # In-memory 모드
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
    driver-class-name: org.h2.Driver

  servlet:
    multipart:
      max-file-size: 1GB  # 업로드 가능한 최대 파일 크기
      max-request-size: 1GB  # 요청의 전체 크기

  jpa:
    hibernate:
      ddl-auto: create  # 개발 편의를 위해 create, 필요에 따라 create-drop/update 등 지정
    properties:
      hibernate:
        format_sql: true
        # show_sql: true  # SQL 로그가 필요할 경우 주석 해제

  data:
    web:
      pageable:
        default-page-size: 10
        max-page-size: 2000
        one-indexed-parameters: true

  redis:
    host: localhost
    port: 6379
    # password:  # 비밀번호가 있을 경우 추가
    # database: 0

  mail:
    host: smtp.gmail.com
    port: 587
    username: "YOUR_EMAIL@gmail.com"  # 실제 Gmail 주소
    password: "YOUR_EMAIL_APP_PASSWORD"  # Gmail 앱 비밀번호 (2단계 인증 시 생성되는 App Password)
    protocol: smtp
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          ssl:
            trust: smtp.gmail.com

logging.level:
  org.hibernate.SQL: debug
  # org.hibernate.type: trace  # 필요하면 주석 해제 (쿼리 파라미터까지 로깅)

jwt:
  secret: "YOUR_JWT_SECRET_KEY"      # JWT 서명에 사용할 비밀 키
  expiration: 3600000               # JWT 만료 시간 (밀리초 단위, 예: 1시간)

imp:
  key: "PORTONE_API_KEY"            # 아임포트(PortOne) API Key (필요 시)
  secret: "PORTONE_API_SECRET"      # 아임포트(PortOne) Secret Key (필요 시)

```
### 5.4 스프링 부트 빌드 & 실행
1. Gradle 빌드
```
./gradlew clean build
```
    - (Windows 환경은 gradlew.bat clean build)
2. 애플리케이션 실행
```
java -jar build/libs/Improve_Fream_Back-0.0.1-SNAPSHOT.jar
```
    - 또는 IDE에서 Spring Boot 메인 클래스 실행

3. H2 콘솔 접속 (개발용)
    - 브라우저에서 http://localhost:8080/h2-console
    - JDBC URL: jdbc:h2:mem:testdb, 사용자: sa
    - 접속 후 테이블/데이터 확인 가능

### 5.5 테스트 데이터 삽입 (DataInitializer)
본 프로젝트에는 DataInitializer 클래스를 통해 테스트용 기본 데이터가 자동으로 삽입됩니다.
- 위치 
    ```
    src/main/java/Fream_back/improve_Fream_Back/config/DataInitializer.java
    ```
- 주요 역할:
  - 기본 사용자(User), 관리자(Admin) 계정 생성
    - 주소(Address), 은행계좌(BankAccount) 등록
    - 상품(Brand, Category, Product, ProductColor, ProductSize) 생성
    - 주문(Order), 입찰(OrderBid, SaleBid), 배송(Shipment), 창고(WarehouseStorage) 등 리셀 프로세스 예시 데이터
    - 공지사항(Notice), FAQ, 검수(Inspection), 알림(Notification) 등의 샘플 데이터
스프링 부트 애플리케이션이 시작될 때, CommandLineRunner를 구현한 이 클래스의 run() 메서드가 실행되어 개발/테스트 환경에서 편리하게 샘플 데이터를 확인할 수 있습니다.
```
주의: 운영(Production) 환경에서는 보통 이 클래스를 비활성화하거나, @Profile("local") 처리하여 실행되지 않도록 합니다.
```
## 6. 추가 정보 (기획 의도, 도메인 분석 등)
아래 내용은 프로젝트의 배경, 도메인 설계, ERD 등을 담고 있습니다.  
필요하신 분만 펼쳐서 확인하세요.

<details>
<summary><strong>기획 의도 & 프로젝트 목표</strong></summary>
   
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



## 재설계 1차 개발 : 12월 9일~ 12월 13일
  1. **사용자 도메인**
  2. **고객센터 도메인**
## 재설계 2차 개발 : 12월 13일 ~ 12월 20일
  1. **상품 도메인**
    
## 재설계 3차 개발 : 12월 20일 ~ 25년 1월 1일
  1. **구매 도메인**
  2. **판매 도메인**
  3. **결제 도메인**
  4. **배송 도메인**
  5. **스타일 도메인**
     
</details>

<details>
<summary><strong>도메인 분석 & ERD</strong></summary>
   
# 도메인 단위 도메인 분석

## 1. 창고 (Warehouse)
### 창고 도메인 연관 엔티티
- WarehouseStorage: 창고 메인 엔티티.
  - User: 창고 보관 사용자.
  - Order: 창고와 연관된 주문.
  - Sale: 창고와 연관된 판매.

## 2. 유저 (User) + 주소 (Address)
### 유저 도메인 연관 엔티티
- User: 유저 메인 엔티티.
- Profile: 유저의 프로필.
- Point: 유저 포인트.
- Follow: 유저 팔로우.
- BlockedProfile: 유저 차단.
- BankAccount: 유저 정산 계좌.
- Address: 유저 주소록.
- PaymentInfo: 유저 결제 정보.

## 3. 스타일 (Style)   
### 스타일 도메인 연관 엔티티
- Style: 스타일 메인 엔티티.
- MediaUrl: 스타일에 포함된 미디어 URL.
- StyleComment: 스타일 댓글.
- StyleLike: 스타일 좋아요.
- StyleInterest: 스타일 관심 등록.
- StyleOrderItem: 스타일 관련 주문 아이템.
  
##  4. 배송 (Shipment)
### 배송 도메인 연관 엔티티
- OrderShipment: 구매자 배송 정보.
- SellerShipment: 판매자 배송 정보.
    
## 5. 판매 (Sale)
### 판매 도메인 연관 엔티티
- Sale: 판매 메인 엔티티.
- SaleBankAccount: 판매 정산 계좌.
- SellerShipment: 판매자 배송 정보.
- SaleBid: 판매 입찰 정보.
- ProductSize: 판매 상품의 사이즈 정보.

##  6. 상품 (Product)
### 상품 도메인 연관 엔티티
- Product: 상품 메인 엔티티.
- ProductColor: 상품 색상 정보.
- ProductSize: 상품 사이즈 정보.
- ProductImage: 상품 이미지.
- ProductDetailImage: 상품 상세 이미지.
- ProductPriceHistory: 상품 가격 변동 내역.
- Interest: 상품 관심 등록.
- Brand: 상품 브랜드.
- Category: 상품 카테고리.
- Collection: 상품 컬렉션.


##  7. 결제 (Payment)
### 결제 도메인 연관 엔티티
- Payment: 결제 메인 엔티티 (상속 구조).
  - GeneralPayment: 일반 결제.
  - CardPayment: 카드 결제.
  - AccountPayment: 계좌 결제.
- PaymentInfo: 유저 결제 정보.

##  8. 주문 (Order)
### 주문 도메인 연관 엔티티
- Order: 주문 메인 엔티티.
- OrderItem: 주문 아이템.
- OrderBid: 주문 입찰 정보.
- WarehouseStorage: 창고 보관 정보

##  9. 알림 (Notification)
### 알림 도메인 연관 엔티티
- Notification: 알림 메인 엔티티.

##  10. 공지사항 (Notice)
### 공지사항 도메인 연관 엔티티
- Notice: 공지사항 메인 엔티티.
- NoticeImage: 공지사항 이미지.

##  11. 검수 기준 (Inspection)
### 검수 기준 도메인 연관 엔티티
- InspectionStandard: 검수 기준 메인 엔티티.
- InspectionStandardImage: 검수 기준 이미지.

##  12. FAQ
### FAQ 도메인 연관 엔티티
- FAQ: FAQ 메인 엔티티.
- FAQImage: FAQ 이미지.

# 전체 엔티티 ERD
![image](https://github.com/user-attachments/assets/17a9805c-1f3d-4da9-9310-e3efde4b6967)

</details>


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








