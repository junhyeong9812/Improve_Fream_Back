package Fream_back.improve_Fream_Back.product.elasticsearch.service;

import Fream_back.improve_Fream_Back.order.repository.OrderBidRepository;
import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.elasticsearch.index.ProductColorIndex;
import Fream_back.improve_Fream_Back.product.elasticsearch.repository.ProductColorEsRepository;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import Fream_back.improve_Fream_Back.style.repository.StyleRepository;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductColorSearchService {

    private final ElasticsearchOperations esOperations;
    private final ProductColorEsRepository productColorEsRepository;
    private final StyleRepository styleRepository;       // <- custom
    private final OrderBidRepository orderBidRepository; // <- custom
    /**
     * 고급 검색 (멀티매치 + 오타 허용 + 동의어 등)
     */
    public Page<ProductSearchResponseDto> searchToDto(
            String keyword,
            List<Long> categoryIds,
            List<String> genders,
            List<Long> brandIds,
            List<Long> collectionIds,
            List<String> colorNames,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice,
            SortOption sortOption,
            Pageable pageable
    ) {
        // 1) 우선 ES 검색 (지금까지 작성한 search(...) 메서드를 재사용)
        Page<ProductColorIndex> pageResult = search(
                keyword, categoryIds, genders, brandIds,
                collectionIds, colorNames, sizes,
                minPrice, maxPrice, sortOption, pageable
        );

        // 2) ProductColorIndex → ProductSearchResponseDto 변환
        List<ProductSearchResponseDto> dtoList = pageResult.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // 3) colorIds 추출
        List<Long> colorIds = dtoList.stream()
                .map(ProductSearchResponseDto::getColorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!colorIds.isEmpty()) {
            // 4) styleCount, tradeCount Map
            Map<Long, Long> styleCountMap = styleRepository.styleCountByColorIds(colorIds);
            Map<Long, Long> tradeCountMap = orderBidRepository.tradeCountByColorIds(colorIds);

            // 5) 주입
            dtoList.forEach(dto -> {
                Long cId = dto.getColorId();
                dto.setStyleCount(styleCountMap.getOrDefault(cId, 0L));
                dto.setTradeCount(tradeCountMap.getOrDefault(cId, 0L));
            });
        }

        // 6) 결과 반환
        return new PageImpl<>(
                dtoList,
                pageResult.getPageable(),
                pageResult.getTotalElements()
        );
    }

    public Page<ProductColorIndex> search(
            String keyword,
            List<Long> categoryIds,
            List<String> genders,
            List<Long> brandIds,
            List<Long> collectionIds,
            List<String> colorNames,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice,
            SortOption sortOption,   // 새로 추가: 정렬 기준 (price, releaseDate, etc.) + order (asc/desc)
            Pageable pageable       // 새로 추가: 페이징 (page=..., size=...)
    ) {
        // 1) 최상위 BoolQuery
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 2) 키워드가 있으면 MultiMatch + Fuzzy
        if (keyword != null && !keyword.isBlank()) {
            // MultiMatch: 여러 필드(productName, brandName, etc.)에 한 번에 매칭
            // fuzziness("AUTO") -> 오타 허용
            MultiMatchQuery.Builder multiMatchBuilder = new MultiMatchQuery.Builder()
                    .fields("productName", "productEnglishName", "brandName", "categoryName", "collectionName","colorName")
                    .query(keyword)
                    .fuzziness("AUTO")       // 오타 자동 보정
                    .maxExpansions(50)      // 오타 교정 시 대체 단어 최대치
                    .prefixLength(1);       // 오타 허용하기 전 최소 몇 글자 일치해야 하는지
            // etc... (operator(Operator.And) 등 옵션 추가 가능)

            // BoolQuery.must(multiMatchQuery)
            boolBuilder.must(new Query.Builder()
                    .multiMatch(multiMatchBuilder.build())
                    .build());
        }

        // 3) categoryIds (OR 조건)
        if (categoryIds != null && !categoryIds.isEmpty()) {
            BoolQuery.Builder catBuilder = new BoolQuery.Builder();
            for (Long catId : categoryIds) {
                catBuilder.should(new Query.Builder()
                        .term(t -> t.field("categoryId").value(catId))
                        .build());
            }
            boolBuilder.must(new Query.Builder().bool(catBuilder.build()).build());
        }

        // 4) gender (OR 조건)
        if (genders != null && !genders.isEmpty()) {
            BoolQuery.Builder genderBuilder = new BoolQuery.Builder();
            for (String g : genders) {
                String esGenderValue = convertGender(g);
                genderBuilder.should(new Query.Builder()
                        .term(t -> t.field("gender").value(esGenderValue))
                        .build());
            }
            boolBuilder.must(new Query.Builder().bool(genderBuilder.build()).build());
        }

        // 5) brandIds (OR 조건)
        if (brandIds != null && !brandIds.isEmpty()) {
            BoolQuery.Builder brandBuilder = new BoolQuery.Builder();
            for (Long bId : brandIds) {
                brandBuilder.should(new Query.Builder()
                        .term(t -> t.field("brandId").value(bId))
                        .build());
            }
            boolBuilder.must(new Query.Builder().bool(brandBuilder.build()).build());
        }

        // 6) collectionIds (OR 조건)
        if (collectionIds != null && !collectionIds.isEmpty()) {
            BoolQuery.Builder colBuilder = new BoolQuery.Builder();
            for (Long cId : collectionIds) {
                colBuilder.should(new Query.Builder()
                        .term(t -> t.field("collectionId").value(cId))
                        .build());
            }
            boolBuilder.must(new Query.Builder().bool(colBuilder.build()).build());
        }

        // 7) colorNames
        if (colorNames != null && !colorNames.isEmpty()) {
            BoolQuery.Builder colorBuilder = new BoolQuery.Builder();
            for (String cName : colorNames) {
                // colorName 필드는 text로 매핑되어 있고 부분매치/동의어 처리되길 원한다면 matchQuery
                colorBuilder.should(new Query.Builder()
                        .match(m -> m.field("colorName").query(cName))
                        .build());
            }
            boolBuilder.must(new Query.Builder().bool(colorBuilder.build()).build());
        }

        // 8) sizes (OR 조건, Keyword Array)
        if (sizes != null && !sizes.isEmpty()) {
            BoolQuery.Builder sizeBuilder = new BoolQuery.Builder();
            for (String sz : sizes) {
                sizeBuilder.should(new Query.Builder()
                        .term(t -> t.field("sizes").value(sz)) // sizes: Keyword array
                        .build());
            }
            boolBuilder.must(new Query.Builder().bool(sizeBuilder.build()).build());
        }

        // 9) minPrice / maxPrice (Range)
        if (minPrice != null && minPrice > 0) {
            boolBuilder.must(rangeQuery("minPrice", null, minPrice));
            // 혹은 "maxPrice >= minPrice" 로 할 수도 있음
        }
        if (maxPrice != null && maxPrice > 0) {
            boolBuilder.must(rangeQuery("maxPrice", maxPrice, null));
            // "maxPrice >= userMaxPrice" 방식
        }

        // (추가) "조건이 전혀 없으면" → match_all
        BoolQuery builtBool = boolBuilder.build();
        boolean noConditions = builtBool.must().isEmpty()
                && builtBool.should().isEmpty()
                && builtBool.filter().isEmpty()
                && builtBool.mustNot().isEmpty();

        // 최종 BoolQuery 빌드
        Query finalQuery;
        if (noConditions) {
            // 아무 필터가 없을 때는 match_all
            finalQuery = new Query.Builder()
                    .matchAll(m->m)
                    .build();
        } else {
            // 기존 BoolQuery 사용
            finalQuery = new Query.Builder()
                    .bool(builtBool)
                    .build();
        }

        // 10) NativeQuery로 감싸기
//        NativeQuery nativeQuery = NativeQuery.builder()
//                .withQuery(finalQuery)
//                .build();
        // 3) NativeQuery 생성
        var nativeQueryBuilder = NativeQuery.builder()
                .withQuery(finalQuery);

        // 4) 정렬 처리 (SortOption)
        if (sortOption != null && sortOption.getField() != null) {
            String rawField = sortOption.getField();
            if (rawField.equalsIgnoreCase("releaseDate")) {
                rawField = "releaseDate";
            } else if (rawField.equalsIgnoreCase("interestCount")) {
                rawField = "interestCount";
            } else if (rawField.equalsIgnoreCase("price")) {
                rawField = "minPrice";
            } else {
                rawField = "productId";
            }

            co.elastic.clients.elasticsearch._types.SortOrder finalOrder =
                    (sortOption.getOrder() != null && sortOption.getOrder().equalsIgnoreCase("desc"))
                            ? co.elastic.clients.elasticsearch._types.SortOrder.Desc
                            : co.elastic.clients.elasticsearch._types.SortOrder.Asc;

            final String finalField = rawField;   // <-- 람다 내에서 쓸 최종 값

            nativeQueryBuilder.withSort(s ->
                    s.field(f -> f.field(finalField).order(finalOrder))
            );
        } else {
            // sortOption이 없거나 field가 없으면 디폴트 정렬: productId asc
            nativeQueryBuilder.withSort(s -> s.field(f -> f.field("productId").order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)));
        }

        // 5) 페이징 처리
        //    Spring Data Elasticsearch의 "NativeQuery"도 .withPageable() 지원
        //    만약 pageable이 null이면 page=0, size=20 같은 디폴트로 세팅
        Pageable finalPageable = pageable != null ? pageable : PageRequest.of(0, 20);
        nativeQueryBuilder.withPageable(finalPageable);

        NativeQuery nativeQuery = nativeQueryBuilder.build();

        // 6) 검색 실행 (Page 기능)
        SearchHits<ProductColorIndex> searchHits = esOperations.search(nativeQuery, ProductColorIndex.class);

        // 7) SearchHits → Page 로 변환
        //    Spring Data ES 4.x+에서 Page 구현체를 얻으려면 별도 변환 필요
        //    간단히 “searchHits.stream()”를 Collect해서 PageImpl 만들 수도 있음
        List<ProductColorIndex> content = searchHits.getSearchHits().stream()
                .map(h -> h.getContent())
                .collect(Collectors.toList());

        // 총 개수(ES에서 track_total_hits=true 인 경우에만 정확히 나올 수 있음),
        // 또는 searchHits.getTotalHits()가 approximate 일 수 있음
        long totalHits = searchHits.getTotalHits();

        // PageImpl
        Page<ProductColorIndex> pageResult = new PageImpl<>(
                content,
                finalPageable,
                totalHits
        );

        return pageResult;

//        // 11) 검색 실행
//        SearchHits<ProductColorIndex> hits = esOperations.search(nativeQuery, ProductColorIndex.class);
//
//        // 결과 반환
//        return hits.getSearchHits().stream()
//                .map(h -> h.getContent())
//                .collect(Collectors.toList());
    }

    /**
     * RangeQuery 빌더 예시: 아래 처럼 min / max 인자를 받아 RangeQuery 만들기
     */
    private Query rangeQuery(String field, Integer gte, Integer lte) {
        return new Query.Builder()
                .range(r -> {
                    var b = r.field(field);
                    if (gte != null) b.gte(JsonData.of(gte));
                    if (lte != null) b.lte(JsonData.of(lte));
                    return b;
                })
                .build();
    }

    private String convertGender(String genderKeyword) {
        switch (genderKeyword.toUpperCase()) {
            case "남자":
            case "MALE":
                return "MALE";
            case "여자":
            case "FEMALE":
                return "FEMALE";
            case "어린이":
            case "KIDS":
                return "KIDS";
            case "공용":
            case "UNISEX":
                return "UNISEX";
            default:
                return "UNISEX";
        }
    }
    private ProductSearchResponseDto toDto(ProductColorIndex idx) {
        // thumbnailUrl 필드가 인덱스에 있다면 그대로 매핑
        return ProductSearchResponseDto.builder()
                .id(idx.getProductId())                 // productId
                .name(idx.getProductName())             // productName
                .englishName(idx.getProductEnglishName())
                .releasePrice(idx.getReleasePrice())
                .thumbnailImageUrl(idx.getThumbnailUrl())  // 인덱스에 넣었다면
                .price(idx.getMinPrice())               // 최저 구매가
                .colorName(idx.getColorName())
                .colorId(idx.getColorId())
                .interestCount(idx.getInterestCount())
                .build();
    }
}
