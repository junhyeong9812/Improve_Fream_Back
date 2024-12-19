package Fream_back.improve_Fream_Back.product.service.product;

import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.repository.ProductDetailResponseDto;
import Fream_back.improve_Fream_Back.product.repository.ProductQueryDslRepository;
import lombok.RequiredArgsConstructor;
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
            String sortField,
            String sortOrder,
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
                sortField,
                sortOrder,
                pageable
        );
    }

    public ProductDetailResponseDto getProductDetail(Long productId, String colorName) {
        return productQueryDslRepository.findProductDetail(productId, colorName);
    }
}

