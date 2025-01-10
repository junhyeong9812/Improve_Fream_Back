package Fream_back.improve_Fream_Back.product.elasticsearch.service;

import Fream_back.improve_Fream_Back.product.elasticsearch.index.ProductColorIndex;
import Fream_back.improve_Fream_Back.product.elasticsearch.repository.ProductColorEsRepository;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductColorSearchService {

    private final ElasticsearchOperations esOperations;
    private final ProductColorEsRepository productColorEsRepository;

    /**
     * 고급 검색 (멀티매치 + 오타 허용 + 동의어 등)
     */
    public List<ProductColorIndex> search(
            String keyword,
            List<Long> categoryIds,
            List<String> genders,
            List<Long> brandIds,
            List<Long> collectionIds,
            List<String> colorNames,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice
    ) {
        // 1) 최상위 BoolQuery
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 2) 키워드가 있으면 MultiMatch + Fuzzy
        if (keyword != null && !keyword.isBlank()) {
            // MultiMatch: 여러 필드(productName, brandName, etc.)에 한 번에 매칭
            // fuzziness("AUTO") -> 오타 허용
            MultiMatchQuery.Builder multiMatchBuilder = new MultiMatchQuery.Builder()
                    .fields("productName", "productEnglishName", "brandName", "categoryName", "collectionName")
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

        // 최종 BoolQuery 빌드
        Query finalQuery = new Query.Builder()
                .bool(boolBuilder.build())
                .build();

        // 10) NativeQuery로 감싸기
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .build();

        // 11) 검색 실행
        SearchHits<ProductColorIndex> hits = esOperations.search(nativeQuery, ProductColorIndex.class);

        // 결과 반환
        return hits.getSearchHits().stream()
                .map(h -> h.getContent())
                .collect(Collectors.toList());
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
}
