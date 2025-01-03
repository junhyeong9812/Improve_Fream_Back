# Style API 기능 및 로직 정리

## 1. 엔드포인트 개요

`Style`(스타일) 관련 작업을 수행하기 위한 엔드포인트로, 다음과 같은 주요 기능을 제공합니다:

- **스타일 생성**
- **스타일 수정**
- **스타일 삭제**
- **스타일 조회(상세, 필터링, 프로필별)**
- **스타일 뷰 카운트 증가**
- **스타일 좋아요 토글**
- **스타일 관심 토글**
- **댓글 생성**
- **댓글 수정**
- **댓글 삭제**
- **댓글 좋아요 토글**

이 모든 기능은 보안이 적용되어 있으며, 일반적으로 **로그인**이 필요한 작업은 헤더의 토큰을 기반으로 인증됩니다. 단, 일부 조회성 API의 경우 권한이 필요하지 않을 수 있습니다. (프로젝트 정책에 따라 달라질 수 있음)

---

## 2. 엔드포인트 상세 설명

### 2.1 스타일 생성
**엔드포인트:**
```
POST /api/styles/commands
```
**필요 권한:** 로그인 사용자

**요청 파라미터 (`multipart/form-data`):**
- **orderItemIds** (필수, `List<Long>`): 스타일에 연결할 `OrderItem`들의 ID 리스트
- **content** (필수, `String`): 스타일 텍스트 내용
- **mediaFiles** (선택, `List<MultipartFile>`): 이미지/동영상 등 업로드할 미디어 파일들

**예시 요청:**
```
POST /api/styles/commands
Header:
  Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data

Body(form-data):
  orderItemIds: 123, 124, ...
  content: "이 스타일 멋지죠?"
  mediaFiles: [파일1, 파일2, ...]
```

**응답 데이터:**
```json
{
  "id": 100
}
```
- `id`: 새로 생성된 스타일의 ID

**기능 요약:**
1. 헤더의 토큰을 통해 사용자 이메일을 추출하여 프로필 식별.
2. 전달받은 `orderItemIds`로 각 `OrderItem`을 조회 후, 새로 생성한 `Style`에 매핑.
3. 업로드된 미디어 파일이 있을 경우, 서버에 저장한 뒤 `MediaUrl` 엔티티로 등록.
4. 생성된 스타일 ID 반환.

---

### 2.2 스타일 수정
**엔드포인트:**
```
PUT /api/styles/commands/{styleId}
```
**필요 권한:** 로그인 사용자 (본인이 작성한 스타일이거나, 관리자 권한 등 정책에 맞게 설정)

**요청 파라미터 (`multipart/form-data`):**
- **content** (선택, `String`): 수정할 스타일 텍스트
- **newMediaFiles** (선택, `List<MultipartFile>`): 새로 추가할 미디어 파일
- **existingUrlsFromFrontend** (선택, `List<String>`): **유지**할 기존 미디어 URL 리스트

**예시 요청:**
```
PUT /api/styles/commands/100
Header:
  Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data

Body(form-data):
  content: "스타일 내용을 이렇게 수정했어요!"
  newMediaFiles: [새로운파일1, 새로운파일2...]
  existingUrlsFromFrontend: ["http://.../1.png", "http://.../2.png"]
```

**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. `styleId`를 통해 기존 스타일 조회.
2. 요청에 포함되지 않은 기존 미디어(= `existingUrlsFromFrontend`에 없는 파일)는 서버에서 삭제.
3. 새로 업로드된 파일이 있으면 서버에 저장하고 `MediaUrl` 엔티티로 추가 등록.
4. `content`가 전달되면 텍스트 내용 업데이트.
5. 상태 코드 200 반환.

---

### 2.3 스타일 삭제
**엔드포인트:**
```
DELETE /api/styles/commands/{styleId}
```
**필요 권한:** 로그인 사용자 (본인이 작성한 스타일이거나, 관리자 권한 등 정책에 맞게 설정)

**예시 요청:**
```
DELETE /api/styles/commands/100
Header:
  Authorization: Bearer {JWT_TOKEN}
```
**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. `styleId`로 스타일 조회.
2. 해당 스타일 및 연관된 데이터(`MediaUrl`, `StyleOrderItem` 등)를 삭제 (JPA cascade 혹은 로직 처리).
3. 상태 코드 200 반환.

---

### 2.4 스타일 뷰 카운트 증가
**엔드포인트:**
```
POST /api/styles/commands/{styleId}/view
```
**필요 권한:** 없음 (조회 시 증가하는 로직이라면 누구나 가능)

**예시 요청:**
```
POST /api/styles/commands/100/view
```

**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. `styleId`로 스타일 조회.
2. 해당 스타일의 `viewCount` 1 증가.
3. 상태 코드 200 반환.

---

### 2.5 스타일 상세 조회
**엔드포인트:**
```
GET /api/styles/queries/{styleId}
```
**필요 권한:** 없음

**예시 요청:**
```
GET /api/styles/queries/100
```

**응답 데이터 예시:**
```json
{
  "id": 100,
  "profileName": "홍길동",
  "profileImageUrl": "http://.../profile.png",
  "content": "이 스타일 멋지죠?",
  "mediaUrls": [
    "http://.../image1.png",
    "http://.../image2.png"
  ],
  "likeCount": 10,
  "commentCount": 5,
  "productInfos": [
    {
      "productName": "제품1",
      "productEnglishName": "Product1",
      "thumbnailImageUrl": "http://.../thumb1.png",
      "minSalePrice": 99000
    }
  ]
}
```
**기능 요약:**
1. `styleId`로 해당 스타일 정보를 조회.
2. 작성자 정보(`profileName`, `profileImageUrl`), 스타일 콘텐츠(`content`), 미디어 URL, 좋아요 수, 댓글 수, 관련 상품 정보 등을 함께 반환.

---

### 2.6 스타일 목록 조회 (필터링)
**엔드포인트:**
```
GET /api/styles/queries
```
**필요 권한:** 없음

**요청 파라미터 (QueryString):**
- `brandName` (선택)
- `collectionName` (선택)
- `categoryId` (선택)
- `isMainCategory` (선택)
- `profileName` (선택)
- `sortBy` (선택) : `"popular"` 또는 `"latest"` 등

**예시 요청:**
```
GET /api/styles/queries?brandName=Nike&sortBy=popular&page=0&size=10
```

**응답 데이터 예시 (페이징):**
```json
{
  "content": [
    {
      "id": 100,
      "profileName": "홍길동",
      "profileImageUrl": "http://.../profile.png",
      "content": "Nike 신발 리뷰 스타일입니다",
      "mediaUrl": "http://.../image1.png",
      "viewCount": 100,
      "likeCount": 10
    },
    {
      "id": 101,
      "profileName": "김영수",
      "profileImageUrl": "http://.../profile2.png",
      "content": "최신 나이키 신발 리뷰",
      "mediaUrl": "http://.../image2.png",
      "viewCount": 50,
      "likeCount": 5
    }
  ],
  "pageable": { ... },
  "totalElements": 2,
  "totalPages": 1
}
```

**기능 요약:**
1. 필터 파라미터(브랜드, 카테고리 등)에 맞게 조건 검색.
2. 결과를 페이징 처리(`Pageable`)하여 반환.
3. 정렬 기준(`popular`= 좋아요 수 기준, `latest`= 최신 업로드 기준 등)을 적용.

---

### 2.7 프로필별 스타일 목록 조회
**엔드포인트:**
```
GET /api/styles/queries/profile/{profileId}
```
**필요 권한:** 없음

**요청 파라미터 (QueryString):**
- `page`, `size` 등 페이징 관련 파라미터

**예시 요청:**
```
GET /api/styles/queries/profile/50?page=0&size=10
```
**응답 데이터 예시 (페이징):**
```json
{
  "content": [
    {
      "id": 100,
      "mediaUrl": "http://.../image1.png",
      "likeCount": 10
    },
    {
      "id": 101,
      "mediaUrl": "http://.../image2.png",
      "likeCount": 5
    }
  ],
  "pageable": { ... },
  "totalElements": 2,
  "totalPages": 1
}
```
- `ProfileStyleResponseDto` 형식으로 반환

**기능 요약:**
1. 특정 프로필(`profileId`)이 작성한 스타일 목록을 조회.
2. 페이징 정보를 포함해 반환.

---

### 2.8 스타일 좋아요 토글
**엔드포인트:**
```
POST /api/styles/likes/commands/{styleId}/toggle
```
**필요 권한:** 로그인 사용자

**예시 요청:**
```
POST /api/styles/likes/commands/100/toggle
Header:
  Authorization: Bearer {JWT_TOKEN}
```
**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. 이미 좋아요 상태면 좋아요를 취소(삭제).
2. 좋아요 상태가 아니면 좋아요를 등록.
3. 상태 코드 200 반환.

---

### 2.9 스타일 관심 토글
**엔드포인트:**
```
POST /api/styles/interests/commands/{styleId}/toggle
```
**필요 권한:** 로그인 사용자

**예시 요청:**
```
POST /api/styles/interests/commands/100/toggle
Header:
  Authorization: Bearer {JWT_TOKEN}
```
**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. 이미 관심(스크랩) 상태면 관심을 취소(삭제).
2. 관심 상태가 아니면 새로운 관심 등록.
3. 상태 코드 200 반환.

---

### 2.10 댓글 생성
**엔드포인트:**
```
POST /api/styles/comments/commands
```
**필요 권한:** 로그인 사용자

**요청 바디 (JSON):**
```json
{
  "styleId": 100,
  "content": "댓글 내용입니다.",
  "parentCommentId": 10
}
```
- `parentCommentId`가 존재하면 대댓글로 처리

**응답 데이터 예시:**
```json
{
  "id": 200,
  "style": {
    "id": 100,
    ...
  },
  "profile": {
    "id": 50,
    ...
  },
  "content": "댓글 내용입니다.",
  "parentComment": {
    "id": 10,
    ...
  }
}
```

**기능 요약:**
1. 로그인 사용자의 프로필을 조회하고 `styleId`에 해당하는 스타일을 조회.
2. 부모 댓글(`parentCommentId`)이 있는 경우 대댓글 처리.
3. 댓글 생성 후 생성된 댓글 정보 반환.

---

### 2.11 댓글 수정
**엔드포인트:**
```
PUT /api/styles/comments/commands/{commentId}
```
**필요 권한:** 로그인 사용자 (본인이 작성한 댓글이거나, 관리자 등 정책에 맞게 설정)

**요청 바디 (JSON):**
```json
{
  "updatedContent": "수정된 댓글입니다."
}
```

**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. `commentId`로 댓글 조회.
2. 댓글 내용(`updatedContent`)을 수정.
3. 상태 코드 200 반환.

---

### 2.12 댓글 삭제
**엔드포인트:**
```
DELETE /api/styles/comments/commands/{commentId}
```
**필요 권한:** 로그인 사용자 (본인이 작성한 댓글이거나, 관리자 등 정책에 맞게 설정)

**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. `commentId`로 댓글 조회.
2. 해당 댓글 삭제 시, 자식 대댓글도 함께 삭제(cascade).
3. 상태 코드 200 반환.

---

### 2.13 댓글 좋아요 토글
**엔드포인트:**
```
POST /api/styles/comments/likes/commands/{commentId}/toggle
```
**필요 권한:** 로그인 사용자

**예시 요청:**
```
POST /api/styles/comments/likes/commands/300/toggle
Header:
  Authorization: Bearer {JWT_TOKEN}
```
**응답 데이터:**
- 상태 코드 200 (OK), Body는 없음

**기능 요약:**
1. `commentId`에 해당하는 댓글에 대해 이미 좋아요 상태면 삭제.
2. 좋아요 상태가 아니면 새로 좋아요 등록.
3. 상태 코드 200 반환.

---

## 3. 보안 및 인증

1. **토큰 인증**
    - `Authorization: Bearer {JWT_TOKEN}` 헤더에서 JWT 토큰 추출.
    - 토큰 검증 성공 시, 사용자 정보를 Security Context에 저장.
    - 이후 서비스 계층에서 `SecurityUtils.extractEmailFromSecurityContext()` 등을 통해 사용자 이메일 획득.

2. **권한 확인**
    - 일반적으로 스타일/댓글을 작성, 수정, 삭제하는 API는 **로그인**이 필요한 접근.
    - 작성자 본인 여부, 관리자 권한 여부 등은 비즈니스 로직에서 체크.

3. **파일 저장 경로**
    - 미디어 파일은 `System.getProperty("user.dir") + "/styles/{styleId}/"` 경로 등에 저장 (프로젝트 정책에 따라 변경 가능).
    - 삭제 시 해당 경로에서 실제 파일 삭제 후, DB(`MediaUrl`) 기록도 제거.

---

## 4. 기타 사항
- **페이징 처리**: `GET /api/styles/queries` 및 `GET /api/styles/queries/profile/{profileId}` 등의 엔드포인트는 `page`, `size`, `sort` 파라미터 등을 통해 페이징 처리.
- **정렬 기준**: `popular(인기순)`, `latest(최신순)` 등의 정렬 로직은 커스텀 쿼리나 스펙을 통해 구현.
- **예외 처리**: 존재하지 않는 `styleId` 또는 `commentId`를 요청 시 `404 Not Found` 혹은 `400 Bad Request` 등을 반환하도록 구성.
- **대댓글 구조**: `parentCommentId`가 존재하면 대댓글로 취급, 부모-자식 관계를 설정하고, 제거 시 cascade 삭제.

