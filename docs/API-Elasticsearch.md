# Elasticsearch 연동 로직 정리

본 문서는 **Spring Boot**와 **Elasticsearch**를 연동하여,
1) **RDB(MySQL 등)에 있는 데이터를 인덱스화**(Bulk Insert) 하고,
2) **Elasticsearch**에서 **고급 검색**(유사 검색, 필터, 정렬 등)을 수행한 뒤,
3) 필요하다면 **DB(QueryDSL)** 재조회로 최종 정보를 획득  
   하는 과정을 코드 구조와 함께 설명합니다.

---

## 1. 인덱스(Index) 구조 및 초기화

### 1.1 `ProductColorIndex` 클래스
- 실제 DB 엔티티(`ProductColor`) 중, **검색**에 필요한 필드만 뽑아서 **Elasticsearch**에 저장할 “인덱스 모델”입니다.

```java
@Document(indexName = "product-colors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductColorIndex {
    @Id
    private Long colorId;  // 문서 _id

    private Long productId;

    @Field(type = FieldType.Text)
    private String productName;
    @Field(type = FieldType.Text)
    private String productEnglishName;
    @Field(type = FieldType.Text)
    private String brandName;
    @Field(type = FieldType.Text)
    private String categoryName;
    @Field(type = FieldType.Text)
    private String collectionName;
    @Field(type = FieldType.Text)
    private String colorName;

    @Field(type = FieldType.Keyword)
    private String gender; // MALE, FEMALE, KIDS, UNISEX

    private Long brandId;
    private Long categoryId;
    private Long collectionId;

    private int releasePrice;
    private int minPrice;  // 해당 color 내 사이즈들 중 최저가
    private int maxPrice;  // 최대가
    private Long interestCount;

    @Field(type = FieldType.Keyword)
    private List<String> sizes;  // ex) ["250","260","270"] etc.
}
```

- `@Document(indexName="product-colors")`로 지정하면, Elasticsearch에 `product-colors`라는 인덱스로 매핑됩니다.

---

### 1.2 인덱스 초기 적재 (Bulk Insert)

#### **ProductColorIndexQueryRepository**
DB의 `ProductColor` + 연관된 `Product`, `Brand`, `Category` 등등을 QueryDSL로 한 번에 조회할 수 있는 Repository.

```java
@Repository
@RequiredArgsConstructor
public class ProductColorIndexQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ProductColor> findAllForIndexing() {
        return queryFactory
                .selectFrom(QProductColor.productColor)
                .leftJoin(QProductColor.productColor.product, QProduct.product).fetchJoin()
                .leftJoin(QProduct.product.brand, QBrand.brand).fetchJoin()
                .leftJoin(QProduct.product.category, QCategory.category).fetchJoin()
                .leftJoin(QProduct.product.collection, QCollection.collection).fetchJoin()
                .leftJoin(QProductColor.productColor.sizes, QProductSize.productSize).fetchJoin()
                .leftJoin(QProductColor.productColor.interests, QInterest.interest).fetchJoin()
                .distinct()
                .fetch();
    }
}
```

#### **ProductColorIndexingService**
위 DB 조회 결과를 `ProductColorIndex`로 변환하여 Elasticsearch에 `saveAll()` 수행.

```java
@Service
@RequiredArgsConstructor
public class ProductColorIndexingService {

    private final ProductColorIndexQueryRepository queryRepository;
    private final ProductColorEsRepository productColorEsRepository; // ElasticsearchRepository

    @Transactional(readOnly = true)
    public void indexAllColors() {
        List<ProductColor> colorList = queryRepository.findAllForIndexing();

        List<ProductColorIndex> indexList = colorList.stream()
            .map(this::toIndex)
            .collect(Collectors.toList());

        productColorEsRepository.saveAll(indexList);
    }

    private ProductColorIndex toIndex(ProductColor pc) {
        Product p = pc.getProduct();

        int minPrice = pc.getSizes().stream().mapToInt(ProductSize::getPurchasePrice).min().orElse(p.getReleasePrice());
        int maxPrice = pc.getSizes().stream().mapToInt(ProductSize::getPurchasePrice).max().orElse(p.getReleasePrice());

        List<String> sizes = pc.getSizes().stream()
            .map(ProductSize::getSize)
            .collect(Collectors.toList());

        long interestCount = pc.getInterests().size();

        return ProductColorIndex.builder()
                .colorId(pc.getId())
                .productId(p.getId())
                .productName(p.getName())
                .productEnglishName(p.getEnglishName())
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .collectionName(p.getCollection() != null ? p.getCollection().getName() : null)
                .brandId(p.getBrand() != null ? p.getBrand().getId() : null)
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .collectionId(p.getCollection() != null ? p.getCollection().getId() : null)
                .colorName(pc.getColorName())
                .gender(p.getGender().name()) // "MALE","FEMALE","KIDS","UNISEX"
                .releasePrice(p.getReleasePrice())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .interestCount(interestCount)
                .sizes(sizes)
                .build();
    }
}
```

#### **서버 기동 시 자동 수행**
`@PostConstruct` 혹은 별도 컴포넌트에서 `indexAllColors()` 호출.

```java
@Component
@RequiredArgsConstructor
public class ProductColorIndexInitializer {
    private final ProductColorIndexingService indexingService;

    @PostConstruct
    public void initIndex() {
        indexingService.indexAllColors();
    }
}
```

---

## 2. 검색 로직 (Elasticsearch)

### 2.1 검색 흐름 요약
- 클라이언트에서 `/api/es/products?...` 형태로 검색 파라미터 전달.
- Controller에서 `ProductColorSearchService.search(...)` 호출.
- Elasticsearch에서 고급 검색 수행 후 색인된 `ProductColorIndex` 반환.
- 검색 결과에서 `colorId` 추출 → 필요 시 DB(QueryDSL) 재조회.

### 2.2 **ProductColorSearchService**

ElasticsearchOperations 및 새로운 Java Client를 사용한 검색 예시.

```java
@Service
@RequiredArgsConstructor
public class ProductColorSearchService {

    private final ElasticsearchOperations esOperations;

    public List<ProductColorIndex> search(
            String keyword,
            List<Long> categoryIds,
            List<String> genders,
            List<Long> brandIds,
            ...
    ) {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        if (keyword != null && !keyword.isBlank()) {
            MultiMatchQuery.Builder multiBuilder = new MultiMatchQuery.Builder()
                    .fields("productName","productEnglishName","brandName","categoryName","collectionName")
                    .query(keyword)
                    .fuzziness("AUTO");
            boolBuilder.must(new Query.Builder().multiMatch(multiBuilder.build()).build());
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            BoolQuery.Builder catBuilder = new BoolQuery.Builder();
            for (Long catId : categoryIds) {
                catBuilder.should(QueryBuilders.term().field("categoryId").value(catId).build());
            }
            boolBuilder.must(new Query.Builder().bool(catBuilder.build()).build());
        }

        Query finalQuery = new Query.Builder().bool(boolBuilder.build()).build();

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(finalQuery)
            .build();

        SearchHits<ProductColorIndex> hits = esOperations.search(nativeQuery, ProductColorIndex.class);

        return hits.getSearchHits().stream()
                .map(h -> h.getContent())
                .collect(Collectors.toList());
    }
}
```

---

## 3. Elasticsearch + DB(QueryDSL) 결합

### 3.1 고급 검색 + 페이징/정렬 로직

실무에서는 Elasticsearch에서 검색한 결과를 바탕으로 DB를 재조회하는 과정을 자주 사용합니다.

#### **Controller**

```java
@GetMapping
public ResponseEntity<Page<ProductSearchResponseDto>> searchProducts(
    @ModelAttribute ProductSearchDto searchDto,
    @ModelAttribute SortOption sortOption,
    Pageable pageable
) {
    List<ProductColorIndex> esResults = productColorSearchService.search(...);

    List<Long> colorIds = esResults.stream()
        .map(ProductColorIndex::getColorId)
        .distinct().toList();

    Page<ProductSearchResponseDto> pageResult =
        productQueryService.searchProductsByColorIds(colorIds, sortOption, pageable);

    return ResponseEntity.ok(pageResult);
}
```

- QueryDSL로 `colorId` 목록을 기반으로 최종 데이터를 DB에서 조회.
- 페이징/정렬은 DB에서 최신 정보 기준으로 수행.

---

## 4. 정리

### Elasticsearch 인덱스 설계
- 검색에 필요한 필드만 저장 (`ProductColorIndex` 등).
- RDB 전체 데이터를 넣지 않고, 최소한의 “검색용” 필드 구성.

### Bulk Insert (초기 인덱싱)
- 서버 기동 시 `indexAllColors()` 호출 → DB에서 전량 조회 후 Elasticsearch에 저장.
- 상품/데이터 변경 시 ES 갱신.

### 검색 시나리오
- Elasticsearch에서 키워드, 필터, RangeQuery 수행.
- 결과 문서 ID 기반으로 DB(QueryDSL) 재조회.
- 최종 페이징 및 정렬 적용.

---

**장점**
- 고급 검색 기능: 오타 교정, 동의어 처리, 점수 기반 검색 등.
- 대규모 데이터에서도 빠른 검색 성능 제공.
- 최신 데이터 정합성 보장.

**추가 고려**
- Analyzer 및 Token Filter 설정: 동의어, 형태소 분석.
- Reindex / Bulk API: 대규모 데이터 일괄 처리.
- Alias 활용: 무중단 인덱스 교체.

이상으로, Elasticsearch 연동 로직을 정리했습니다.  
검색과 데이터 정합성을 분리하여 성능과 유연성을 확보할 수 있습니다.

