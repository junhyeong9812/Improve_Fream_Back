# Fream 리빌딩 프로젝트

## 1. 프로젝트 개요
- 기존 Kream 클론 프로젝트에서 부족했던 점을 개선하고, e-Commerce와 커뮤니티 기능을 통합한 리셀 플랫폼을 다시 설계/구현한 프로젝트입니다.
### 프로젝트 아키텍처
![image](https://github.com/user-attachments/assets/528be0c1-bf3e-4a41-8038-bd1a877421c8)


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
### 5.6 GeoLite2-City.mmdb 설정 (GeoIP 서비스)

본 프로젝트는 사용자 위치 정보를 조회하기 위해 GeoLite2-City.mmdb 파일을 사용합니다.
이 파일은 MaxMind에서 제공하며, 해당 파일을 다운로드하여 프로젝트 리소스 폴더에 추가해야 합니다.

#### 파일 다운로드 및 설정
1. **GeoLite2-City.mmdb 다운로드**
    - [MaxMind GeoLite2 다운로드 페이지](https://dev.maxmind.com/geoip/geolite2-free-geolocation-data?lang=en)에 접속합니다.
    - 계정을 생성하거나 로그인 후 GeoLite2-City 데이터베이스 파일을 다운로드합니다.

2. **프로젝트 리소스 폴더에 파일 추가**
    - 다운로드 받은 `GeoLite2-City.mmdb` 파일을 프로젝트의 `src/main/resources` 디렉토리에 복사합니다.

3. **GeoIP 서비스 초기화**
    - GeoIP 기능을 사용하는 `GeoIPService` 클래스는 `src/main/resources`에 위치한 `GeoLite2-City.mmdb` 파일을 자동으로 로드합니다.
    - 파일이 없거나 잘못된 경로에 위치하면 애플리케이션 실행 시 에러가 발생하므로, 반드시 올바르게 설정해야 합니다.

#### 파일 경로 확인 및 주의사항
- 파일 경로: `src/main/resources/GeoLite2-City.mmdb`
- **운영 환경 보안 주의:** GeoLite2-City 데이터베이스는 민감한 정보를 포함하지 않으나, 파일 접근 권한을 제한하여 보안을 강화해야 합니다.

#### 예제 코드 (GeoIPService 클래스에서의 파일 경로 설정)
```java
public class GeoIPService {

    private final DatabaseReader databaseReader;

    public GeoIPService() throws IOException {
        File database = new File(getClass().getClassLoader().getResource("GeoLite2-City.mmdb").getFile());
        this.databaseReader = new DatabaseReader.Builder(database).build();
    }

    // 위치 정보 조회 메서드 생략
}
```

#### 테스트 방법
1. 애플리케이션 실행 후 특정 IP 주소로 위치 정보 조회 테스트를 수행합니다.
2. GeoIP 관련 메서드 호출 시 정확한 위치 정보(나라, 지역, 도시)가 반환되면 설정이 올바르게 완료된 것입니다.

---

### 주의사항
- **파일 업데이트:** GeoLite2-City 데이터베이스는 정기적으로 업데이트되므로, 최신 버전을 주기적으로 다운로드하여 사용을 권장합니다.
- **MaxMind 라이선스 준수:** GeoLite2 데이터베이스는 MaxMind의 라이선스 조건에 따라 사용해야 합니다.



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

## 13. Access Log
### Access Log 도메인 연관 엔티티
- UserAccessLog: 사용자 접근 로그 메인 엔티티.

## 14. Weather Data
### Weather Data 도메인 연관 엔티티
- WeatherData: 날씨 데이터 메인 엔티티.

# 전체 엔티티 ERD
![image](https://github.com/user-attachments/assets/17a9805c-1f3d-4da9-9310-e3efde4b6967)

</details>


# API 명세서

## 설정 관련 문서
- [README-Config](docs/README-Config.md)
- [README-DataInitializer](docs/README-DataInitializer.md)
- [README-Security](docs/README-Security.md)
- [README-Utils](docs/README-Utils.md)


## 1. 사용자(User) / 프로필(Profile) / 팔로우(Follow) / 차단(Block)

#### API 문서 : [API-User](docs/API-User.md)

### 1.1 /api/users
   - POST /register : 사용자 회원가입
   - POST /login : 사용자 로그인 (JWT 토큰 발급)
   - POST /find-email : 전화번호로 이메일 찾기
   - POST /reset-password : 이메일·전화번호 확인 후 비밀번호 초기화 가능 여부 확인
   - POST /reset-password-sandEmail : 임시 비밀번호 이메일 전송
   - POST /reset : 비밀번호 변경
   - PUT /update-login-info : 로그인 정보(이메일·비밀번호 등) 업데이트
   - GET /login-info : 로그인 정보 조회
   - DELETE /delete-account : 회원 탈퇴



### 1.2 /api/profiles
   - GET / : 내 프로필 조회
   - PUT / (multipart) : 프로필 업데이트(이미지, 공개범위 등)
   - GET /{profileId}/image : 특정 프로필 이미지 조회


### 1.3 /api/follows
   - POST /{profileId} : 해당 프로필 팔로우
   - DELETE /{profileId} : 팔로우 취소
   - GET /followers : 내 팔로워 목록 조회
   - GET /followings : 내 팔로잉 목록 조회


### 1.4 /api/profiles/blocked
   - POST / : 특정 프로필 차단
   - DELETE / : 특정 프로필 차단 해제
   - GET / : 내가 차단한 프로필 목록 조회

### 1.5 /api/bank-account
   - POST / : 판매 정산 계좌 생성·수정
   - GET / : 판매 정산 계좌 조회
   - DELETE / : 판매 정산 계좌 삭제

--------------------------------------------

## 2. 주소(Address)

#### API 문서 : [API-User](docs/API-User.md)

### /api/addresses
   - POST / : 주소록 생성
   - PUT / : 주소록 수정
   - DELETE /{addressId} : 주소록 삭제
   - GET / : 내 주소 목록 조회
   - GET /{addressId} : 특정 주소 조회

--------------------------------------------

## 3. FAQ

#### API 문서 : [API-FAQ](docs/API-FAQ.md)

### /faqs
   - POST / (multipart) : FAQ 생성 (관리자 권한)
   - PUT /{id} (multipart) : FAQ 수정 (관리자 권한)
   - DELETE /{id} : FAQ 삭제 (관리자 권한)
   - GET / : FAQ 목록 조회(카테고리 필터 가능)
   - GET /{id} : FAQ 단일 조회
   - GET /search : FAQ 검색

--------------------------------------------

## 4. 검수(Inspection)

#### API 문서 : [API-Inspection](docs/API-Inspection.md)

### /inspections
   - POST / (multipart) : 검수 기준 생성 (관리자 권한)
   - PUT /{id} (multipart) : 검수 기준 수정 (관리자 권한)
   - DELETE /{id} : 검수 기준 삭제 (관리자 권한)
   - GET / : 검수 기준 목록 조회(카테고리 필터 가능)
   - GET /{id} : 검수 기준 단일 조회

--------------------------------------------

## 5. 공지사항(Notice)

#### API 문서 : [API-Notice](docs/API-Notice.md)

### /notices
   - POST / (multipart) : 공지사항 생성 (관리자 권한)
   - PUT /{noticeId} (multipart) : 공지사항 수정
   - DELETE /{noticeId} : 공지사항 삭제
   - GET /{noticeId} : 단일 공지 조회
   - GET /search : 공지사항 검색
   - GET / : 공지사항 목록 조회(카테고리 필터 가능)
   - GET /files/{fileName} : 첨부파일 미리보기

--------------------------------------------

## 6. 알림(Notification)

#### API 문서 : [API-Notification](docs/API-Notification.md)

### /api/notifications
   - POST / : 특정 유저 알림 생성
   - POST /broadcast : 전체 유저에게 알림 생성
   - PATCH /{id}/read : 알림 읽음 처리
   - GET /filter/category : 카테고리별 알림 조회
   - GET /filter/type : 유형별 알림 조회
   - GET /filter/category/read-status : 카테고리 + 읽음여부 필터
   - GET /filter/type/read-status : 유형 + 읽음여부 필터
   - (WebSocket) @MessageMapping("/ping") : Ping 처리(TTL 갱신)

--------------------------------------------

## 7. 주문(Order) & 구매 입찰(OrderBid)

#### API 문서 : [API-OrderAndSale](docs/API-OrderAndSale.md)

### 7.1 /api/order-bids (Command)
   - POST / : 구매 입찰(OrderBid) 생성
   - DELETE /{orderBidId} : 구매 입찰 삭제
   - POST /instant : 즉시 구매 생성(결제·배송 등)

### 7.2 /api/order-bids (Query)
   - GET / : 내 구매 입찰 목록 조회 (bidStatus·orderStatus 필터)
   - GET /count : 내 구매 입찰 상태 카운트 조회

### 7.3 /api/orders (Command)
   - POST /{orderId}/process-payment-shipment : 결제 & 배송처리

--------------------------------------------

## 8. 결제(Payment)

#### API 문서 : [API-Payment](docs/API-Payment.md)

### /api/payment-info
   - POST / : 결제 정보(PaymentInfo) 생성
   - DELETE /{id} : 결제 정보 삭제
   - GET / : 결제 정보 목록 조회
   - GET /{id} : 결제 정보 단일 조회
   - POST /test-payment : 테스트 결제/환불 시나리오

--------------------------------------------

## 9. 상품(Product) & 카테고리(Category) & 브랜드(Brand) & 컬렉션(Collection) & 관심상품(Interest)

#### API 문서 : [API-Product](docs/API-Product.md)

### 9.1 /api/products
   - GET / : 상품 검색(키워드, 카테고리, 브랜드, 컬러, 사이즈, 가격범위 등)
   - GET /{productId}/detail : 상품 상세 조회(colorName)
   - GET /{productId}/images : 상품 이미지 파일 제공
   - POST / : 상품 등록(관리자 권한)
   - PUT /{productId} : 상품 수정(관리자 권한)
   - DELETE /{productId} : 상품 삭제(관리자 권한)

### 9.2 /api/product-colors
   - POST /{productId} : 상품 컬러 추가(썸네일, detailImages 등)
   - PUT /{productColorId} : 상품 컬러 수정
   - DELETE /{productColorId} : 상품 컬러 삭제

### 9.3 /api/interests
   - GET /{userId} : 유저의 관심상품 목록 조회
   - POST /{productColorId}/toggle : 관심상품 토글(추가/삭제)

### 9.4 /api/categories
   - POST / : 카테고리 생성(관리자 권한)
   - PUT /{categoryId} : 카테고리 수정
   - DELETE /{categoryId} : 카테고리 삭제
   - GET /main : 메인(상위) 카테고리 목록
   - GET /sub/{mainCategoryName} : 하위 카테고리 목록

### 9.5 /api/brands
   - POST / : 브랜드 생성(관리자 권한)
   - PUT /{brandId} : 브랜드 수정
   - DELETE /{brandName} : 브랜드 삭제
   - GET / : 모든 브랜드 조회
   - GET /{brandName} : 특정 브랜드 조회

### 9.6 /api/collections
   - POST / : 컬렉션 생성(관리자 권한)
   - PUT /{collectionId} : 컬렉션 수정
   - DELETE /{collectionName} : 컬렉션 삭제
   - GET / : 전체 컬렉션 조회

--------------------------------------------

## 10. 판매(Sale) & 판매 입찰(SaleBid)

#### API 문서 : [API-OrderAndSale](docs/API-OrderAndSale.md)

### 10.1 /api/sale-bids (Command)
   - POST / : 판매 입찰(SaleBid) 생성
   - DELETE /{saleBidId} : 판매 입찰 삭제
   - POST /instant : 즉시 판매 생성

### 10.2 /api/sale-bids (Query)
   - GET / : 내 판매 입찰 목록 조회(saleBidStatus, saleStatus 필터)
   - GET /count : 내 판매 입찰 상태 카운트 조회

### 10.3 /api/sales
   - POST /{saleId}/shipment : 판매자 배송정보 등록(택배사, 운송장번호)

--------------------------------------------

## 11. 배송(Shipment)

#### API 문서 : [API-Shipment](docs/API-Shipment.md)

### 11.1 /api/shipments/order
   - PATCH /{shipmentId}/status : 구매자(Order) 배송 정보 업데이트

### 11.2 /api/shipments/seller
   - POST / : 판매자(Seller) 배송 정보 생성
   - PATCH /{shipmentId} : 판매자 배송 정보 업데이트

--------------------------------------------

## 12. 스타일(Style) & 댓글(StyleComment) & 좋아요(StyleLike) & 관심(StyleInterest)

#### API 문서 : [API-Style](docs/API-Style.md)

### 12.1 /api/styles/commands
   - POST / : 스타일 생성(내용, 이미지 등)
   - POST /{styleId}/view : 스타일 조회수 증가
   - PUT /{styleId} : 스타일 수정
   - DELETE /{styleId} : 스타일 삭제

### 12.2 /api/styles/comments/commands
   - POST / : 댓글 생성
   - PUT /{commentId} : 댓글 수정
   - DELETE /{commentId} : 댓글 삭제

### 12.3 /api/styles/comments/likes/commands
   - POST /{commentId}/toggle : 댓글 좋아요 토글

### 12.4 /api/styles/interests/commands
   - POST /{styleId}/toggle : 스타일 관심 토글

### 12.5 /api/styles/likes/commands
   - POST /{styleId}/toggle : 스타일 좋아요 토글

### 12.6 /api/styles/queries
   - GET /{styleId} : 특정 스타일 상세 조회
   - GET / : 스타일 목록 조회(필터)
   - GET /profile/{profileId} : 해당 프로필의 스타일 목록

## 13. 접근 로그(Access Log)

#### API 문서 : [API-AccessLog](docs/API-AccessLog.md)

### 13.1 /access-log/commands
- POST /create : 사용자 접근 로그 생성
    - 클라이언트의 IP 주소, 브라우저 정보, 디바이스 타입 등 다양한 정보를 기반으로 접근 로그를 저장합니다.

---

## 14. 날씨 데이터(Weather)

#### API 문서 : [API-Weather](docs/API-Weather.md)

### 14.1 /api/weather/queries
- GET /current : 현재 시간과 가장 가까운 날씨 데이터 조회
    - 최근 데이터를 기준으로 가장 가까운 시간대의 날씨 정보를 반환합니다.
- GET /today : 당일의 모든 날씨 데이터 조회
    - 오늘 날짜에 해당하는 모든 날씨 데이터를 시간 순으로 반환합니다.






