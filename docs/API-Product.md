### ProductAPI 엔드포인트 기능 및 로직 정리

#### 1. 엔드포인트 개요
`ProductAPI`는 상품과 관련된 여러 작업을 수행하기 위한 엔드포인트를 제공합니다. 주요 기능은 아래와 같습니다:

1. **브랜드(Brand)**
    - 브랜드 생성
    - 브랜드 수정
    - 브랜드 삭제
    - 브랜드 전체 조회
    - 단일 브랜드 조회
2. **카테고리(Category)**
    - 카테고리 생성
    - 카테고리 수정
    - 카테고리 삭제
    - 메인 카테고리 목록 조회
    - 특정 메인 카테고리에 대한 서브 카테고리 조회
3. **컬렉션(Collection)**
    - 컬렉션 생성
    - 컬렉션 수정
    - 컬렉션 삭제
    - 컬렉션 전체 조회
4. **관심상품(Interest)**
    - 관심 상품 토글(추가/삭제)
    - 사용자의 관심 상품 목록 조회(페이징)
5. **상품(Product)**
    - 상품 생성
    - 상품 수정
    - 상품 삭제
    - 상품 검색(필터, 페이징, 정렬 지원)
    - 상품 상세 조회(특정 색상 기준)
    - 상품 이미지 다운로드(썸네일, 상세 이미지 등)
6. **상품색상(ProductColor)**
    - 상품 색상 생성(썸네일/일반/상세 이미지 업로드)
    - 상품 색상 수정
    - 상품 색상 삭제

이 모든 기능 중 **관리자 권한이 필요한 작업**은 `Authorization` 헤더의 JWT 토큰을 통해 인증 과정을 거칩니다. (권한이 없는 사용자 시도 시 `403 Forbidden` 응답)

---

#### 2. 엔드포인트 상세 설명

##### 2.1 브랜드(Brand)

###### 2.1.1 브랜드 생성
**엔드포인트**  
`POST /api/brands`

**필요 권한**
- 관리자

**요청 헤더**
```plaintext
Authorization: Bearer {토큰}
```

**요청 바디** (`BrandRequestDto`)
```json
{
  "name": "브랜드명"
}
```

**응답 바디** (`BrandResponseDto`)
```json
{
  "id": "생성된 브랜드 ID",
  "name": "브랜드명"
}
```

**기능**
1. 전달된 브랜드명을 사용하여 브랜드 정보를 생성합니다.
2. 이미 존재하는 브랜드명일 경우 예외가 발생할 수 있습니다.
3. 생성된 브랜드 정보(`id`, `name`)를 반환합니다.

---

###### 2.1.2 브랜드 수정
**엔드포인트**  
`PUT /api/brands/{brandId}`

**필요 권한**
- 관리자

**요청 헤더**
```plaintext
Authorization: Bearer {토큰}
```

**요청 바디** (`BrandRequestDto`)
```json
{
  "name": "수정할 브랜드명"
}
```

**응답 바디** (`BrandResponseDto`)
```json
{
  "id": "브랜드 ID",
  "name": "수정된 브랜드명"
}
```

**기능**
1. `brandId`에 해당하는 브랜드 데이터를 조회합니다.
2. 요청 바디에 담긴 정보로 브랜드명을 수정합니다(더티체크).
3. 수정된 브랜드 정보를 반환합니다.

---

###### 2.1.3 브랜드 삭제
**엔드포인트**  
`DELETE /api/brands/{brandName}`

**필요 권한**
- 관리자

**요청 헤더**
```plaintext
Authorization: Bearer {토큰}
```

**응답 데이터**
- 상태 코드 204 (No Content)

**기능**
1. `{brandName}`으로 브랜드를 조회합니다.
2. 해당 브랜드와 연결된 상품이 존재한다면, 먼저 상품을 삭제해야 합니다.
3. 연결된 상품이 없다면 브랜드를 삭제합니다.
4. 성공 시 204 No Content 반환.

---

###### 2.1.4 브랜드 전체 조회
**엔드포인트**  
`GET /api/brands`

**필요 권한**
- 없음

**응답 바디** (`BrandResponseDto` 배열)
```json
[
  {
    "id": "브랜드 ID",
    "name": "브랜드명"
  },
  ...
]
```

**기능**
1. 모든 브랜드를 이름 내림차순으로 조회합니다.
2. 조회된 브랜드 리스트를 반환합니다.

---

###### 2.1.5 단일 브랜드 조회
**엔드포인트**  
`GET /api/brands/{brandName}`

**필요 권한**
- 없음

**응답 바디** (`BrandResponseDto`)
```json
{
  "id": "브랜드 ID",
  "name": "브랜드명"
}
```

**기능**
1. `{brandName}`에 해당하는 브랜드 정보를 조회합니다.
2. 조회된 정보를 반환합니다.
3. 생성된 상품 정보를 반환합니다.

---

### 2.5.2 상품 수정

**엔드포인트**
`PUT /api/products/{productId}`

**필요 권한**

- 관리자

**요청 헤더**

```plaintext
Authorization: Bearer {토큰}
```

**요청 바디** (`ProductUpdateRequestDto`)

```json
{
  "name": "새로운 상품명",
  "price": 10000,
  "description": "업데이트할 상품 설명",
  "categories": ["카테고리1", "카테고리2"],
  "brandId": "브랜드 ID"
}
```

**응답 바디** (`ProductResponseDto`)

```json
{
  "id": "상품 ID",
  "name": "업데이트된 상품명",
  "price": 10000,
  "description": "업데이트된 상품 설명",
  "categories": ["카테고리1", "카테고리2"],
  "brand": {
    "id": "브랜드 ID",
    "name": "브랜드명"
  }
}
```

**기능**

1. `productId`에 해당하는 상품을 조회합니다.
2. 요청 바디의 데이터를 사용해 상품 정보를 업데이트합니다.
    - 상품 이름, 가격, 설명, 카테고리, 브랜드를 수정합니다.
3. 업데이트된 상품 데이터를 반환합니다.

**예외 처리**

- 존재하지 않는 `productId`로 요청한 경우: `404 Not Found`
- 잘못된 카테고리 또는 브랜드 ID로 요청한 경우: `400 Bad Request`

---

### 2.5.3 상품 삭제

**엔드포인트**
`DELETE /api/products/{productId}`

**필요 권한**

- 관리자

**요청 헤더**

```plaintext
Authorization: Bearer {토큰}
```

**응답 데이터**

- 상태 코드: `204 No Content`

**기능**

1. `productId`에 해당하는 상품을 조회합니다.
2. 상품에 연결된 모든 하위 데이터를 삭제합니다.
3. 상품 정보를 삭제합니다.
4. 성공 시 `204 No Content` 상태를 반환합니다.

---

### 2.5.4 상품 검색

**엔드포인트**
`GET /api/products`

**필요 권한**

- 없음

**요청 파라미터**

- `keyword`: 키워드 검색어
- `categoryIds`: 카테고리 ID 목록 (다중)
- `brandIds`: 브랜드 ID 목록 (다중)
- `minPrice`: 최소 가격
- `maxPrice`: 최대 가격
- `sortField`: 정렬 기준 필드
- `sortOrder`: 정렬 순서 (`asc`, `desc`)
- 페이징 관련 파라미터 (`page`, `size`)

**응답 바디** (`Page<ProductSearchResponseDto>`)

```json
{
  "content": [
    {
      "id": "상품 ID",
      "name": "상품명",
      "price": 10000,
      "brand": "브랜드명",
      "categories": ["카테고리1", "카테고리2"],
      "thumbnailUrl": "이미지 URL"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 100,
  "totalPages": 10
}
```

**기능**

1. 여러 필터 조건(keyword, categoryIds, brandIds 등)에 따라 상품을 검색합니다.
2. 정렬 옵션(`sortField`, `sortOrder`)에 따라 정렬된 데이터를 반환합니다.
3. 페이징 처리된 결과를 제공합니다.

---

### 2.5.5 상품 상세 조회

**엔드포인트**
`GET /api/products/{productId}`

**필요 권한**

- 없음

**응답 바디** (`ProductDetailResponseDto`)

```json
{
  "id": "상품 ID",
  "name": "상품명",
  "price": 10000,
  "description": "상품 설명",
  "categories": ["카테고리1", "카테고리2"],
  "brand": {
    "id": "브랜드 ID",
    "name": "브랜드명"
  },
  "images": [
    {
      "url": "이미지 URL",
      "type": "THUMBNAIL"
    }
  ]
}
```

**기능**

1. `productId`에 해당하는 상품 상세 정보를 조회합니다.
2. 상품 정보, 카테고리, 브랜드 및 이미지 데이터를 반환합니다.

---

### 2.5.6 상품 이미지 다운로드

**엔드포인트**
`GET /api/products/{productId}/images/{imageId}`

**필요 권한**

- 없음

**응답 데이터**

- 바이너리 파일 데이터

**기능**

1. `productId` 및 `imageId`를 기준으로 이미지를 조회합니다.
2. 해당 이미지를 바이너리 데이터로 반환합니다.
3. 이미지가 존재하지 않을 경우 `404 Not Found`를 반환합니다.

---

### 2.6 상품 색상(ProductColor)

#### 2.6.1 상품 색상 생성

**엔드포인트**
`POST /api/product-colors/{productId}`

**필요 권한**

- 관리자

**요청 헤더**

```plaintext
Authorization: Bearer {토큰}
```

**요청 데이터** (Multipart 요청)

`ProductColorCreateRequestDto`

```json
{
  "colorName": "색상명",
  "content": "해당 색상 상세 설명",
  "sizes": ["사이즈1", "사이즈2", ...]
}
```

추가 데이터:
- `thumbnailImage`: 대표 이미지 (필수)
- `images`: 추가 이미지 목록 (optional)
- `detailImages`: 상세 페이지 이미지 목록 (optional)

**응답 데이터**

- 상태 코드: `200 OK`

**기능**

1. `productId`로 상품을 조회합니다.
2. 색상명을 검증(허용된 `ColorType`만 가능).
3. 업로드된 이미지들을 서버에 저장 후, DB에 경로를 저장.
4. 해당 상품 색상에 대한 사이즈 정보를 생성.
5. 성공 시 `200 OK` 반환 (필요 시 생성된 `productColorId` 반환 가능).

---

#### 2.6.2 상품 색상 수정

**엔드포인트**
`PUT /api/product-colors/{productColorId}`

**필요 권한**

- 관리자

**요청 헤더**

```plaintext
Authorization: Bearer {토큰}
```

**요청 데이터** (Multipart 요청)

`ProductColorUpdateRequestDto`

```json
{
  "colorName": "수정할 색상명",
  "content": "수정할 상세 설명",
  "existingImages": ["유지할 기존 이미지 URL1", ...],
  "existingDetailImages": ["유지할 기존 상세 이미지 URL1", ...],
  "sizes": ["수정 후 유지될 사이즈 목록"]
}
```

추가 데이터:
- `thumbnailImage`: 새로운 썸네일 (optional)
- `newImages`: 새로 추가할 일반 이미지 (optional)
- `newDetailImages`: 새로 추가할 상세 페이지 이미지 (optional)

**응답 데이터**

- 상태 코드: `200 OK`

**기능**

1. `productColorId`로 기존 색상 정보를 조회.
2. 썸네일 변경 시, 이전 썸네일 이미지를 삭제 후 새로 저장.
3. 기존 일반 이미지는 `existingImages`에 없는 경우 물리 파일 및 DB에서 삭제, `newImages`는 새로 업로드.
4. 상세 이미지는 `existingDetailImages`에 없는 경우 삭제, `newDetailImages`는 새로 업로드.
5. 사이즈 목록을 비교해 기존 것 중 빠진 것은 삭제, 새로 추가된 것은 생성.
6. 수정 완료 후 `200 OK` 반환.

---

#### 2.6.3 상품 색상 삭제

**엔드포인트**
`DELETE /api/product-colors/{productColorId}`

**필요 권한**

- 관리자

**요청 헤더**

```plaintext
Authorization: Bearer {토큰}
```

**응답 데이터**

- 상태 코드: `204 No Content`

**기능**

1. `productColorId`로 색상을 조회.
2. 연결된 관심상품(`Interest`), 사이즈(`ProductSize`), 이미지(`ProductImage`, `ProductDetailImage`) 등을 모두 삭제.
3. 실제 물리 파일도 삭제 후, `ProductColor` 정보를 삭제.
4. 성공 시 `204 No Content` 반환.

---

### 3. 제품 관련 주요 Enum

본 API에서 사용되는 주요 열거형(enum) 타입은 아래와 같습니다.

- **ColorType**: 상품 색상 이름 (예: BLACK, WHITE, RED...)
    - 내부적으로 `displayName`(한글명)을 가질 수 있습니다.

- **GenderType**: 상품 성별 (MALE, FEMALE, KIDS, UNISEX)

- **SizeType**: 카테고리별로 가능한 사이즈 정의 (예: CLOTHING, SHOES, ACCESSORIES 등)

---

### 4. 보안 및 인증

- **관리자 권한 확인**
    - `Authorization` 헤더에 포함된 JWT 토큰으로부터 이메일을 추출.
    - 이메일을 바탕으로 사용자가 관리자 권한을 가지고 있는지 검사. (미인증, 권한이 부족하면 `403` 또는 `401` 응답)

- **파일 저장 경로**
    - 본 예시에서는 `System.getProperty("user.dir") + "/product/{productId}"` 경로에 상품 이미지를 저장.
    - 썸네일, 일반 이미지, 상세 이미지 등은 구분된 파일명 접두어(`thumbnail_`, `ProductImage_`, `ProductDetailImage_`)로 저장.

- **권한이 없는 경우**
    - 관리자 권한이 필요한 엔드포인트를 일반 사용자가 요청하면 `403 Forbidden` 또는 `401 Unauthorized`가 반환.

---



