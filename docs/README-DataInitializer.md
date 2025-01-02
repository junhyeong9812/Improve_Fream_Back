# README-DataInitializer

## 1. 개요
**DataInitializer**는 애플리케이션이 구동될 때 사용자(User), 상품(Product), 공지사항(Notice), FAQ, 검수 기준(Inspection), 알림(Notification), 주문(Order), 판매(Sale), 배송(Shipment) 등 다양한 초기 데이터를 자동으로 생성해 DB에 저장합니다.

---

## 2. 사용자(User) 관련 데이터

### 2.1 사용자(User)
| 구분  | Email                  | Password(암호화 전) | Phone         | ShoeSize  | Role  | Age | Gender | ReferralCode (랜덤8) |
|-------|------------------------|---------------------|---------------|-----------|-------|-----|--------|----------------------|
| user1 | user1@example.com      | password123         | 010-1234-5678 | SIZE_270  | USER  | 25  | MALE   | 예: 1A2B3C4D         |
| user2 | user2@example.com      | password456         | 010-9876-5432 | SIZE_280  | USER  | 30  | FEMALE | 예: 9X8Y7Z6W         |
| admin | admin@example.com      | adminpassword       | 010-0000-0000 | (null)    | ADMIN | 35  | MALE   | 예: AB12CD34         |

**추가 정보:** 각 사용자마다 Profile가 자동 생성됩니다(createDefaultProfile).

### 2.2 주소(Address)
| 구분  | 사용자(Email)          | 수령인   | 전화번호       | 우편번호 | 주소                  | 상세주소        | 기본주소? |
|-------|------------------------|----------|---------------|----------|-----------------------|-----------------|----------|
| user1 | user1@example.com      | 홍길동    | 010-1234-5678 | 12345    | 서울시 강남구 도산대로 | 아파트 101호    | true     |
| user2 | user2@example.com      | 김철수    | 010-9876-5432 | 67890    | 서울시 강서구 화곡로   | 빌라 202호      | true     |
| admin | admin@example.com      | 관리자    | 010-0000-0000 | 54321    | 서울시 종로구 종로     | 사무실 303호    | true     |

### 2.3 은행 계좌(BankAccount)
| 구분  | 사용자(Email)          | 은행명   | 계좌번호       | 예금주    |
|-------|------------------------|----------|---------------|----------|
| user1 | user1@example.com      | 국민은행 | 123-4567-8901 | 홍길동    |
| user2 | user2@example.com      | 신한은행 | 987-6543-2101 | 김철수    |
| admin | admin@example.com      | 우리은행 | 456-7890-1234 | 관리자    |

---

## 3. 상품(Product) 관련 데이터

### 3.1 브랜드(Brand)
| 브랜드 이름        |
|-------------------|
| Nike              |
| New Balance       |
| Adidas            |
| Jordan            |
| Stussy            |
| IAB Studio        |
| NewJeans          |

### 3.2 카테고리(Category)
| 상위 카테고리 | 하위 카테고리        |
|---------------|---------------------|
| Clothing      | Tops               |
| Tops          | Short Sleeve T-Shirts |
| Shoes         | Sneakers           |

### 3.3 상품(Product)
#### Sneakers (1~10)
| 상품 ID | Name                 | EnglishName              | 브랜드         | 카테고리   | ReleasePrice | 색상                       | 사이즈                    | ModelNumber | ReleaseDate | Gender |
|---------|----------------------|--------------------------|----------------|------------|--------------|---------------------------|---------------------------|-------------|-------------|--------|
| 1       | Sneakers Product 1   | Sneakers English Product 1 | Nike           | Sneakers   | 150          | BLACK, GREY, NAVY        | 250, 260, 270, 280, 290  | Model-1     | 2023-01-01  | MALE   |
| 2       | Sneakers Product 2   | Sneakers English Product 2 | Adidas         | Sneakers   | 200          | WHITE, ORANGE, MINT      | 250, 260, 270, 280, 290  | Model-2     | 2023-01-02  | FEMALE |

#### Short Sleeve T-Shirts (11~20)
| 상품 ID | Name                          | EnglishName                       | 브랜드         | 카테고리              | ReleasePrice | 색상          | 사이즈                  | ModelNumber | ReleaseDate | Gender |
|---------|-------------------------------|-----------------------------------|----------------|-----------------------|--------------|---------------|-------------------------|-------------|-------------|--------|
| 11      | Short Sleeve T-Shirts Product 11 | Short Sleeve T-Shirts English Product 11 | Stussy         | Short Sleeve T-Shirts | 650          | BLACK         | S, M, L, XL, XXL       | Model-11    | 2023-01-11  | FEMALE |
| 20      | Short Sleeve T-Shirts Product 20 | Short Sleeve T-Shirts English Product 20 | NewJeans       | Short Sleeve T-Shirts | 1050         | GREY          | S, M, L, XL, XXL       | Model-20    | 2023-01-20  | MALE   |

---

## 4. 공지사항(Notice)
| Notice ID | 카테고리     | 제목                       | 내용                        |
|-----------|--------------|--------------------------|----------------------------|
| 1         | SHOPPING     | SHOPPING 공지사항 제목 1 | SHOPPING 공지사항 내용 1   |
| 11        | SERVICE      | SERVICE 공지사항 제목 1  | SERVICE 공지사항 내용 1    |
| 30        | EVENT        | EVENT 공지사항 제목 10   | EVENT 공지사항 내용 10     |

---

## 5. FAQ
| FAQ ID | 카테고리     | 질문                   | 답변                    |
|--------|--------------|-----------------------|------------------------|
| 1      | SHOPPING     | SHOPPING 질문 1      | SHOPPING 답변 내용 1   |
| 11     | PAYMENT      | PAYMENT 질문 1       | PAYMENT 답변 내용 1    |
| 30     | DELIVERY     | DELIVERY 질문 10     | DELIVERY 답변 내용 10  |

---

## 6. 검수 기준(InspectionStandard)
| ID | 카테고리 | 내용                        |
|----|----------|---------------------------|
| 1  | SHOES    | SHOES 검수 기준 내용       |
| 2  | CLOTHING | CLOTHING 검수 기준 내용    |

---

## 7. 알림(Notification)
| ID | 사용자(Email)          | 카테고리 | 타입             | 메세지                          | isRead |
|----|------------------------|----------|-----------------|---------------------------------|--------|
| 1  | user1@example.com      | SHOPPING | ORDER_CONFIRMED | user1@example.com의 쇼핑 알림: ORDER_CONFIRMED | false  |
| 11 | admin@example.com      | SHOPPING | PAYMENT_DONE    | admin@example.com의 쇼핑 알림: PAYMENT_DONE    | false  |

---

## 8. 주문(Order) 및 입찰(Bid)

### 8.1 OrderBid
| ID | 사용자      | ProductSize          | 입찰 가격 | 상태     |
|----|-------------|----------------------|----------|----------|
| 1  | user1       | Sneakers Product 1, 270 | 5000     | PENDING  |
| 6  | user2       | Sneakers Product 2, 280 | 6000     | MATCHED  |

### 8.2 SaleBid
| ID | 판매자      | ProductSize          | 입찰 가격 | 상태     | 반품 주소    | 우편번호 | 연락처      | 창고보관 |
|----|-------------|----------------------|----------|----------|-------------|----------|-------------|---------|
| 1  | user1       | Sneakers Product 1, 270 | 8000     | PENDING  | 123 Street  | 12345    | 010-1111-2222 | false   |
| 6  | user2       | Sneakers Product 3, 280 | 9000     | MATCHED  | 456 Avenue  | 67890    | 010-3333-4444 | true    |

---

## 9. 실행 후 로그
```
초기 데이터가 성공적으로 생성되었습니다.
```

