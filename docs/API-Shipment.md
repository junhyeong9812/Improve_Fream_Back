### **Shipment API 엔드포인트 기능 및 로직 정리**

---

## **1. 엔드포인트 개요**
Shipment(배송) 관련 작업을 수행하기 위한 엔드포인트로, 다음과 같은 주요 기능을 제공합니다:
- **배송 정보 생성**: 주문 및 판매와 연관된 배송 정보 생성
- **배송 정보 수정**: 배송 상태 또는 송장 번호 업데이트
- **배송 상태 조회 및 업데이트**: 현재 배송 상태를 외부 API(CJ 대한통운)에서 확인하고 업데이트
- [추가] **대량 송장 번호(배송 상태) 자동 갱신**: 스프링 배치를 통해 일정 주기로 일괄 처리
- **배송 상태 관리**: 주문과 연관된 배송 상태 및 알림 처리

이 모든 기능은 관리자 권한이 필요한 작업과 그렇지 않은 작업으로 나뉘며, 인증이 필요한 경우 헤더의 토큰을 기반으로 수행됩니다.

---

## **2. 엔드포인트 상세 설명**

### **2.1 주문 배송 상태 업데이트**
**엔드포인트:**
```
PATCH /api/shipments/order/{shipmentId}/status
```

**요청 헤더:**
- Authorization: Bearer {토큰}

**요청 데이터:**
```json
{
   "courier": "배송사 이름",
   "trackingNumber": "송장 번호"
}
```

**응답 데이터:**
- 상태 코드 200 (OK)

**기능:**
1. `shipmentId`로 배송 정보를 조회.
2. 요청 데이터를 기반으로 배송사 및 송장 번호를 업데이트.
3. 배송 상태를 **IN_TRANSIT**로 변경.
4. 관련된 주문 상태를 **IN_TRANSIT**로 업데이트.
5. 구매자에게 "상품이 배송 중입니다" 알림 전송.
6. Sale 및 SaleBid 상태를 갱신하며 필요한 경우 창고 상태 업데이트.

---

### **2.2 판매자 배송 정보 생성**
**엔드포인트:**
```
POST /api/shipments/seller
```

**요청 헤더:**
- Authorization: Bearer {토큰}

**요청 데이터:**
```json
{
   "saleId": "판매 ID",
   "courier": "배송사 이름",
   "trackingNumber": "송장 번호"
}
```

**응답 데이터:**
```json
{
   "shipmentId": "배송 ID"
}
```

**기능:**
1. `saleId`를 기반으로 Sale 정보를 조회.
2. 요청 데이터를 기반으로 SellerShipment를 생성.
3. Sale과 연관된 배송 정보를 갱신.
4. 창고 보관 상태를 업데이트하거나 **IN_TRANSIT** 상태로 전환.

---

### **2.3 판매자 배송 정보 업데이트**
**엔드포인트:**
```
PATCH /api/shipments/seller/{shipmentId}
```

**요청 헤더:**
- Authorization: Bearer {토큰}

**요청 데이터:**
```json
{
   "courier": "새로운 배송사 이름",
   "trackingNumber": "새로운 송장 번호"
}
```

**응답 데이터:**
- 상태 코드 200 (OK)

**기능:**
1. `shipmentId`를 기반으로 배송 정보를 조회.
2. 요청 데이터를 기반으로 배송사 및 송장 번호를 갱신.
3. SellerShipment의 상태를 자동으로 갱신.

---

### **2.4 배송 상태 업데이트 (자동화)**
**내부 로직:**

**기능:**
1. 배송 상태가 **IN_TRANSIT** 또는 **OUT_FOR_DELIVERY**인 모든 OrderShipment 조회.
2. 외부 API(CJ 대한통운)를 호출하여 현재 배송 상태를 확인.
3. 배송 상태가 **DELIVERED**로 업데이트된 경우:
    - Order 상태를 **COMPLETED**로 변경.
    - 구매자에게 "배송이 완료되었습니다" 알림 전송.
4. 상태가 변경되지 않은 경우 다음 상태로 전환을 확인.

#### 스프링 배치 적용 시나리오
1. **스케줄링**

- @Scheduled(cron = "0 0 */6 * * *") 등으로 6시간마다 배치 Job 실행
- 또는 별도 JobScheduler(Quartz, Kubernetes CronJob 등)로 실행 가능

2. **Batch Job**(updateShipmentStatusesJob)
- Step(updateShipmentStatusesStep)에서 “Chunk-Oriented Processing”으로 아래 로직을 수행
1. **Reader**: JpaPagingItemReader를 사용해 DB에서 **status ∈ {IN_TRANSIT, OUT_FOR_DELIVERY}**인 배송 정보를 50개씩 페이징 조회
2. **Processor**: 각 OrderShipment의 송장번호를 이용해 CJ 대한통운 페이지를 스크래핑(Jsoup).
   - 배송 완료면 ShipmentStatus.DELIVERED, Order → COMPLETED 등 상태 변경 & 알림 전송
   - 배송중이면 IN_TRANSIT, 출발이면 OUT_FOR_DELIVERY
3. **Writer**: 변경된 배송 객체를 DB에 저장
- .faultTolerant().skip(Exception.class).skipLimit(N)로 네트워크/파싱 오류에 대한 스킵·재시도 가능
- 실행 후 배치 메타데이터를 통해 언제 몇 건이 처리되었는지 확인 가능
이 과정을 통해 대량의 송장 상태를 한번에 갱신하면서, “배달 완료” 시점에 주문도 자동으로 마무리 처리할 수 있습니다.


**외부 API 사용:**
- URL: `https://trace.cjlogistics.com/next/tracking.html?wblNo={trackingNumber}`
- HTML 파싱으로 현재 배송 상태 추출.

---

### **2.5 배송 상태 전환 규칙**
`ShipmentStatus`는 다음 상태 전환 규칙을 따릅니다:

- **PENDING** → **SHIPPED**, **CANCELED**
- **SHIPPED** → **IN_TRANSIT**, **RETURNED**, **CANCELED**
- **IN_TRANSIT** → **OUT_FOR_DELIVERY**, **DELAYED**, **CANCELED**
- **OUT_FOR_DELIVERY** → **DELIVERED**, **FAILED_DELIVERY**, **CANCELED**
- **DELIVERED** → 상태 전환 없음
- **RETURNED** → **CANCELED**
- **DELAYED** → **IN_TRANSIT**, **CANCELED**
- **FAILED_DELIVERY** → **RETURNED**, **OUT_FOR_DELIVERY**, **CANCELED**

---

## **3. 보안 및 인증**
1. **관리자 권한 확인**:
    - `Authorization` 헤더의 JWT 토큰을 사용하여 사용자 인증.
    - 토큰에 포함된 권한을 확인하여 관리자 여부를 확인.

2. **송장 번호 보안**:
    - 송장 번호는 서버에서 암호화하여 저장하며, 외부 요청 시 복호화.

---

## **4. 관련 알림 기능**
배송과 연관된 상태 변화 시 다음과 같은 알림이 전송됩니다:
1. **배송 시작**: "상품이 배송 중입니다."
2. **배송 완료**: "상품이 배송 완료되었습니다."
3. **배송 실패**: "배송에 실패했습니다. 고객 센터에 문의해주세요."

---

## **5. 주요 엔티티**

### **5.1 OrderShipment**
- **필드**:
    - `id`: 배송 ID
    - `order`: 연관된 Order 엔티티
    - `courier`: 배송사
    - `trackingNumber`: 송장 번호
    - `status`: 배송 상태

### **5.2 SellerShipment**
- **필드**:
    - `id`: 배송 ID
    - `sale`: 연관된 Sale 엔티티
    - `courier`: 배송사
    - `trackingNumber`: 송장 번호
    - `status`: 배송 상태

---

## **6. 주요 클래스 및 메서드**
### **OrderShipmentCommandService**
- `createOrderShipment()`: 주문 배송 정보 생성
- `updateShipmentStatus()`: 배송 상태 업데이트
- `updateTrackingInfo()`: 송장 번호 업데이트
- `updateShipmentStatuses()`: 외부 API로 배송 상태 업데이트

### **SellerShipmentCommandService**
- `createSellerShipment()`: 판매자 배송 정보 생성
- `updateShipment()`: 판매자 배송 정보 수정

### (25년 1월12일 업데이트) **UpdateShipmentStatusesJobConfig**
- `updateShipmentStatusesJob()`: 배송 상태 자동 갱신용 Job
- `updateShipmentStatusesStep()`: Chunk-Oriented Step
- `shipmentItemReader()`: 상태가 IN_TRANSIT 또는 OUT_FOR_DELIVERY인 배송 목록 조회(JPA 페이징)
- `shipmentItemProcessor()`: CJ대한통운에서 상태 파싱, DELIVERED 시 주문 완료 + 알림
- `shipmentItemWriter()`: 변경된 배송 엔티티 DB 저장
- `.faultTolerant().skip(Exception.class).skipLimit(50)` → 스크래핑 실패 시 Skip


---

## **7. 개선 및 최적화**
1. **외부 API 호출 최적화**:
    - 배송 상태 업데이트 시 배치 프로세스 도입.
2. **에러 처리**:
    - 외부 API 호출 실패 시 로깅 및 재시도 메커니즘 추가.
3. **알림 템플릿 관리**:
    - Notification 템플릿 관리 기능 추가로 알림 메시지의 통일성 확보.

