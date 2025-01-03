### Order & Sale 엔드포인트 기능 및 로직 정리

#### 1. 개요
**Order(주문)**와 **Sale(판매)** 관련 API는 구매자와 판매자가 서로 연결되어 거래를 완결 짓는 기능을 담당합니다.

- **Order**: 주문 관련 (구매 입찰, 결제, 배송, 창고 보관 등)
- **Sale**: 판매 관련 (판매 입찰, 발송, 창고 보관 등)

여기에는 반드시 헤더에 JWT 토큰을 설정해야 하며, 해당 토큰이 Spring Security를 통해 인증된 뒤 사용자 이메일을 SecurityUtils에서 추출하는 방식으로 작동합니다.

또한 결제 방식은 크게 아래 세 가지가 지원되며, 테스트 환경에서는 일부 미구현/간소화되어 있습니다.

1. **General 결제**: 프론트에서 이미 PortOne(아임포트) 결제가 끝난 정보를 백엔드에 저장.
2. **카드 결제**: 사전에 저장된 카드 정보(또는 빌링키)를 통해 결제.
3. **계좌 결제**: 무통장입금 형태로 가정 (실제 입금 확인 로직은 미완성).

---

#### 2. 인증(헤더 토큰)과 사용자 이메일 추출

##### 2.1 SecurityUtils 예시
```java
public class SecurityUtils {

    public static String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다."); // 인증 실패 처리
    }
}
```

- Spring Security에서 관리하는 SecurityContext에 인증 정보가 저장되며,
- `principal`이 문자열(이메일)로 세팅되어 있으면 그대로 반환,
- 인증 실패 시 `IllegalStateException` 발생.

##### 2.2 헤더 설정
API 호출 시 request header에 다음과 같이 토큰 정보를 넣어주어야 합니다:

```
Authorization: Bearer <JWT_TOKEN>
```

또는 실제 코드 상에서:

```java
request.setAttribute("Authorization", "Bearer " + token);
```

이후 Spring Security 필터를 거쳐 토큰 검증이 완료되면,
`SecurityUtils.extractEmailFromSecurityContext()`로 사용자 이메일을 안전하게 획득할 수 있습니다.

---

#### 3. Order API 정리

##### 3.1 OrderBid (구매 입찰)

###### 3.1.1 OrderBid 생성

- **엔드포인트**: `POST /api/order-bids`
- **요청 데이터 (OrderBidRequestDto)**

```json
{
  "userEmail": "사용자 이메일(보통 SecurityContext에서 추출)",
  "productSizeId": 123,
  "bidPrice": 100000
}
```

- **응답 데이터**: 생성된 OrderBid ID(Long)

- **처리 로직**:
    1. `SecurityUtils.extractEmailFromSecurityContext()` → 사용자 이메일 확인.
    2. `orderBidCommandService.createOrderBid()` 내부에서 Order를 생성.
    3. OrderBid 엔티티(status = PENDING) 생성 후 DB 저장.

###### 3.1.2 OrderBid 삭제

- **엔드포인트**: `DELETE /api/order-bids/{orderBidId}`
- **처리 로직**:
    1. `orderBidCommandService.deleteOrderBid(orderBidId)` 호출.
    2. Sale과 연결되어 있다면 예외 → 매칭 후에는 삭제 불가.
    3. 연결된 Order도 함께 삭제.
    4. 최종적으로 OrderBid 삭제.

###### 3.1.3 즉시 구매 (Instant Purchase)

- **엔드포인트**: `POST /api/order-bids/instant`
- **요청 데이터 (InstantOrderRequestDto)**

```json
{
  "saleBidId": 987,
  "addressId": 111,
  "warehouseStorage": false,
  "paymentRequest": {
    "paymentType": "CARD",     // or "ACCOUNT", "GENERAL"
    "paidAmount": 100000,
    "paymentInfoId": 55
  }
}
```

- **응답 데이터**: 생성된 Order ID(Long)

- **처리 로직**:
    1. SaleBid 조회 → 매칭 대상 확인.
    2. `orderCommandService.createInstantOrder(...)` 호출.
    3. OrderItem, OrderShipment 생성.
    4. 결제 진행.
    5. 성공 시 Order 상태 단계별 변경.
    6. OrderBid를 MATCHED 상태로 저장.

###### 3.1.4 OrderBid 목록 조회

- **엔드포인트**: `GET /api/order-bids`
- **응답 데이터**: Page<OrderBidResponseDto>

###### 3.1.5 OrderBid 상태 카운트 조회

- **엔드포인트**: `GET /api/order-bids/count`
- **응답 데이터 (OrderBidStatusCountDto)**

```json
{
  "pendingCount": 3,
  "matchedCount": 2,
  "cancelledOrCompletedCount": 1
}
```

##### 3.2 Order (주문)

###### 3.2.1 결제 및 배송 처리

- **엔드포인트**: `POST /api/orders/{orderId}/process-payment-shipment`
- **요청 데이터 (PayAndShipmentRequestDto)**

```json
{
  "paymentRequest": {
    "paymentType": "CARD",
    "paidAmount": 100000
  },
  "receiverName": "홍길동",
  "receiverPhone": "010-1234-5678",
  "postalCode": "12345",
  "address": "서울시 ...",
  "warehouseStorage": false
}
```

- **처리 로직**:
    1. Order 조회 → 사용자 본인인지 확인.
    2. 결제 진행.
    3. 배송지(OrderShipment) 생성.
    4. 상태 변경(배송 중, 창고 보관 등).

---

### 3.2.2 배송 상태 갱신 (CJ대한통운 연동)

#### 1. 설명
실제 택배사의 배송 상태를 주기적으로 조회하여 시스템의 배송 상태를 최신으로 유지합니다. 현재 구현은 CJ대한통운의 송장 조회를 기반으로 작동하며, 배송 상태에 따라 주문과 관련 엔티티의 상태를 동기화합니다.

#### 2. 주요 로직
- **송장 조회**: CJ대한통운의 웹 페이지에서 송장 번호로 현재 배송 상태를 파싱합니다.
- **상태 업데이트**:
    - "배송 완료" 시 **Order**, **Sale**, **Shipment** 상태를 `COMPLETED`, `DELIVERED`로 변경.
    - 알림(Notification)을 통해 사용자에게 배송 완료 사실을 통지.

#### 3. 코드 예시
```java
@Transactional
public void updateShipmentStatuses() {
    List<OrderShipment> shipments = orderShipmentRepository.findByStatusIn(
        List.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.OUT_FOR_DELIVERY)
    );

    for (OrderShipment shipment : shipments) {
        try {
            String currentStatus = getCurrentTrackingStatus(shipment.getTrackingNumber());
            ShipmentStatus newStatus = mapToShipmentStatus(currentStatus);

            if (newStatus == ShipmentStatus.DELIVERED) {
                shipment.updateStatus(newStatus);
                Order order = shipment.getOrder();
                order.updateStatus(OrderStatus.COMPLETED);

                User buyer = order.getUser();
                notificationCommandService.createNotification(
                    buyer.getId(),
                    NotificationCategory.SHOPPING,
                    NotificationType.BID,
                    "상품이 배송 완료되었습니다. 주문 ID: " + order.getId()
                );
            }

            orderShipmentRepository.save(shipment);
        } catch (Exception e) {
            System.err.println("배송 상태 업데이트 실패: " + e.getMessage());
        }
    }
}
```

#### 4. 활용한 외부 API
- **CJ대한통운 송장 조회**:
    - URL: `https://trace.cjlogistics.com/next/tracking.html?wblNo=<송장번호>`
    - HTML 파싱: Jsoup 라이브러리를 활용하여 배송 상태 데이터를 추출.

#### 5. 상태 매핑 로직
송장 상태를 시스템의 **ShipmentStatus**로 매핑합니다.
```java
private ShipmentStatus mapToShipmentStatus(String statusText) {
    return switch (statusText) {
        case "배송완료" -> ShipmentStatus.DELIVERED;
        case "배송출발" -> ShipmentStatus.OUT_FOR_DELIVERY;
        default -> ShipmentStatus.IN_TRANSIT;
    };
}
```

#### 6. 주기적 상태 갱신
- 이 기능은 스케줄러(Spring Scheduler)를 활용하여 6시간마다 실행되도록 설정합니다.
- 크론 표현식 예시:
  ```java
  @Scheduled(cron = "0 0 */6 * * *")
  public void scheduleShipmentStatusUpdates() {
      updateShipmentStatuses();
  }
  ```

---



#### 4. Sale API 정리

##### 4.1 SaleBid (판매 입찰)

###### 4.1.1 SaleBid 생성

- **엔드포인트**: `POST /api/sale-bids`
- **요청 데이터 (SaleBidRequestDto)**

```json
{
  "productSizeId": 123,
  "bidPrice": 80000,
  "returnAddress": "서울시 강남구 ...",
  "postalCode": "12345",
  "receiverPhone": "010-1234-5678",
  "warehouseStorage": true
}
```

###### 4.1.2 SaleBid 삭제

- **엔드포인트**: `DELETE /api/sale-bids/{saleBidId}`

###### 4.1.3 즉시 판매 (Instant Sale)

- **엔드포인트**: `POST /api/sale-bids/instant`
- **요청 데이터 (InstantSaleRequestDto)**

```json
{
  "orderBidId": 456,
  "returnAddress": "...",
  "postalCode": "11111",
  "receiverPhone": "010-1111-2222"
}
```

##### 4.2 Sale (판매)

###### 4.2.1 판매 발송(배송 정보 등록)

- **엔드포인트**: `POST /api/sales/{saleId}/shipment`
- **요청 데이터 (ShipmentRequestDto)**

```json
{
  "courier": "CJ대한통운",
  "trackingNumber": "1234567890"
}
```

---

#### 5. 결제 방식(카드 / General / 계좌)

##### 카드 결제
- 사전 등록된 카드 정보를 통해 빌링키 요청(테스트용).
- 실제 운영 시 PG사에 카드번호 미보관, 빌링키만 관리.

##### General 결제
- 프론트에서 PortOne(아임포트) 결제 완료 후, impUid 등 결과를 백엔드에 전달.

##### 계좌 결제
- 무통장입금 형태, 실제 입금 확인 로직은 미구현.

---

#### 6. 엔티티 상태(Enum) 요약

##### OrderStatus
```java
PENDING_PAYMENT, PAYMENT_COMPLETED, PREPARING, IN_WAREHOUSE, SHIPMENT_STARTED, COMPLETED
```

##### BidStatus
```java
PENDING, MATCHED, CANCELLED, COMPLETED
```

##### SaleStatus
```java
PENDING_SHIPMENT, IN_TRANSIT, IN_STORAGE, SOLD
```

##### ShipmentStatus
```java
PENDING, SHIPPED, IN_TRANSIT, DELIVERED
```

##### PaymentStatus
```java
PENDING, PAID, REFUND_REQUESTED, REFUNDED
```

##### WarehouseStatus
```java
IN_STORAGE, ASSOCIATED_WITH_ORDER, SOLD
```

---

### 7. 전체 프론 요약

#### 사용자 인증(JWT 토큰)
- API 호출 시 Header에 `Authorization: Bearer <token>` 설정
- Spring Security 인증 → SecurityContext 세팅 → SecurityUtils로 이메일 추출

#### 구매자
- **OrderBid** 생성 → **Order** 연결
- 결제(**PaymentRequestDto**) → **paymentCommandService**
- 배송 or 찾고 보관 → **OrderShipmentCommandService**, **WarehouseStorageCommandService**

#### 판매자
- **SaleBid** 생성 → **Sale** 연결
- 발송(**SellerShipment**) → **Sale.status = IN_TRANSIT** → 이후 검수/찾고/낭찰 등 지정

#### 즉시 매칭
- 구매자: `POST /api/order-bids/instant` → 특정 **SaleBid** 매칭
- 판매자: `POST /api/sale-bids/instant` → 특정 **OrderBid** 매칭
- 생성된 **Order**, **Sale**가 서로 **BidStatus = MATCHED**로 연결

#### 결제 로직
- 카드/General/계좌(무통장)
- 실제 PG 연동은 테스트용 코드(모의) / 원용 시 보안·규격 준수 필요

#### 상호 전이
- **OrderStatus**, **SaleStatus**, **BidStatus** 등을 단계별로 변경
- `canTransitionTo(...)` 로직으로 유효성 체크

### 8. 마무리

#### 헤더 토큰 기본 인증
- 반드시 `Authorization: Bearer <token>` 형태로 전달,
- **SecurityUtils.extractEmailFromSecurityContext()**로 이메일 시행

#### 결제 로직
- **General**: 프론트에서 결제 완료 후 정보만 백에드에 전달
- **카드**: 사전 등록된 카드정보나 빌링키를 이용(테스트용 모의)
- **계좌**: 무통장입금 가정(실제 로직 미완성)

#### 엔티티 상태(Enum)
- **OrderStatus**, **SaleStatus**, **BidStatus**, **PaymentStatus**, **ShipmentStatus**, **WarehouseStatus** 등
- 각각 상태 전이 규칙을 통해 올바르지 않은 프론이라서 정당성 보장

#### 주요 기능
- **OrderBid**(구매 입찰), **SaleBid**(판매 입찰)
- 즉시 구매/판매(Instant)로 두 **Bid**를 매칭
- 결제/배송/찾고보관 등을 **Service** 계층에서 처리

