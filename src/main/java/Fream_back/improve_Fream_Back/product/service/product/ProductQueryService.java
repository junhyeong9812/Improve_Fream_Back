package Fream_back.improve_Fream_Back.product.service.product;

import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.dto.ProductDetailResponseDto;
import Fream_back.improve_Fream_Back.product.repository.ProductQueryDslRepository;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private final ProductQueryDslRepository productQueryDslRepository;

    public Page<ProductSearchResponseDto> searchProducts(
            String keyword,
            List<Long> categoryIds,
            List<GenderType> genders,
            List<Long> brandIds,
            List<Long> collectionIds,
            List<String> colors,
            List<String> sizes,
            Integer minPrice,
            Integer maxPrice,
            SortOption sortOptions,
            Pageable pageable) {
        return productQueryDslRepository.searchProducts(
                keyword,
                categoryIds,
                genders,
                brandIds,
                collectionIds,
                colors,
                sizes,
                minPrice,
                maxPrice,
                sortOptions,
                pageable
        );
    }

    public ProductDetailResponseDto getProductDetail(Long productId, String colorName) {
        try {
            return productQueryDslRepository.findProductDetail(productId, colorName);
        } catch (InvalidDataAccessApiUsageException e) {
            // 데이터 접근 계층의 예외를 명시적인 IllegalArgumentException으로 변환
            throw new IllegalArgumentException("해당 상품 또는 색상이 존재하지 않습니다.", e);
        }
    }
    // “ES colorId 목록”을 기반으로 DB 재조회
    public Page<ProductSearchResponseDto> searchProductsByColorIds(
            List<Long> colorIds,
            SortOption sortOption,
            Pageable pageable
    ) {
        return productQueryDslRepository.searchProductsByColorIds(
                colorIds, sortOption, pageable
        );
    }

}

