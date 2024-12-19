package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.service.product.ProductCommandService;
import Fream_back.improve_Fream_Back.product.service.product.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    // 상품 생성
    @PostMapping
    public ProductCreateResponseDto createProduct(@RequestBody ProductCreateRequestDto request) {
        return productCommandService.createProduct(request);
    }

    // 상품 수정
    @PutMapping("/{productId}")
    public ProductUpdateResponseDto updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto request) {
        return productCommandService.updateProduct(productId, request);
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productCommandService.deleteProduct(productId);
    }

    // 상품 목록 검색
    @GetMapping
    public Page<ProductSearchResponseDto> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<GenderType> genders,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) List<Long> collectionIds,
            @RequestParam(required = false) List<String> colors,
            @RequestParam(required = false) List<String> sizes,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String interested, // 관심 정렬 추가
            Pageable pageable) {
        return productQueryService.searchProducts(
                keyword,
                categoryIds,
                genders,
                brandIds,
                collectionIds,
                colors,
                sizes,
                minPrice,
                maxPrice,
                sortField,
                sortOrder,
                interested, // 관심 정렬 추가
                pageable
        );
    }

    // 상품 상세 조회
    @GetMapping("/{productId}/details")
    public ProductDetailResponseDto getProductDetail(
            @PathVariable Long productId,
            @RequestParam(required = false) String colorName) {
        return productQueryService.getProductDetail(productId, colorName);
    }
}
