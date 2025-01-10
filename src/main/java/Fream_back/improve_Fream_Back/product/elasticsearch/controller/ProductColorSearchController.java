package Fream_back.improve_Fream_Back.product.elasticsearch.controller;

import Fream_back.improve_Fream_Back.product.dto.ProductSearchDto;
import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.elasticsearch.index.ProductColorIndex;
import Fream_back.improve_Fream_Back.product.elasticsearch.service.ProductColorSearchService;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.service.product.ProductQueryService;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/es/products")
public class ProductColorSearchController {

    private final ProductColorSearchService productColorSearchService;
    private final ProductQueryService productQueryService;
    // 기존 ProductSearchDto와 유사한 DTO를 @ModelAttribute로 받는다
    @GetMapping
    public ResponseEntity<Page<ProductSearchResponseDto>> esSearchProducts(
            @ModelAttribute ProductSearchDto searchDto,
            @ModelAttribute SortOption sortOption,  // <-- 쿼리 파라미터 바인딩 예: ?field=price&order=asc
            Pageable pageable
    ) {
        // 1) ES 검색
        List<ProductColorIndex> esResults = productColorSearchService.search(
                searchDto.getKeyword(),
                searchDto.getCategoryIds(),
                convertGenders(searchDto.getGenders()), // String 으로 받을 수도 있음
                searchDto.getBrandIds(),
                searchDto.getCollectionIds(),
                searchDto.getColors(),
                searchDto.getSizes(),
                searchDto.getMinPrice(),
                searchDto.getMaxPrice()
        );



        // colorId만 추출
        List<Long> colorIds = esResults.stream()
                .map(ProductColorIndex::getColorId)
                .distinct()
                .toList();

        Page<ProductSearchResponseDto> pageResult =
                productQueryService.searchProductsByColorIds(colorIds, sortOption, pageable);

        // 4) 응답
        return ResponseEntity.ok(pageResult);
    }

    private ProductSearchResponseDto toDto(ProductColorIndex idx) {
        return ProductSearchResponseDto.builder()
                .id(idx.getProductId())  // 또는 colorId / productId
                .name(idx.getProductName())
                .englishName(idx.getProductEnglishName())
                .releasePrice(idx.getReleasePrice())
                .thumbnailImageUrl("")  // 썸네일 URL은 DB 추가 조회 or 인덱스에 넣어도 됨
                .price(idx.getMinPrice())  // 최저 구매가
                .colorName(idx.getColorName())
                .colorId(idx.getColorId())
                .interestCount(idx.getInterestCount())
                .build();
    }

    private List<String> convertGenders(List<GenderType> genderTypes) {
        // "MALE", "FEMALE", "KIDS", "UNISEX" 문자열 목록으로 변환
        if (genderTypes == null) return Collections.emptyList();
        return genderTypes.stream().map(GenderType::name).collect(Collectors.toList());
    }
}
