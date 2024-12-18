package Fream_back.improve_Fream_Back.product.service.productSize;

import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.product.repository.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductSizeCommandService {

    private final ProductSizeRepository productSizeRepository;

    public void createProductSizes(ProductColor productColor, Category category, List<String> requestedSizes, int releasePrice) {
        // 최상위 카테고리 찾기
        Category rootCategory = findRootCategory(category);

        // SizeType 결정
        SizeType sizeType = determineSizeType(rootCategory.getName());

        // 요청된 사이즈 검증 및 생성
        requestedSizes.forEach(size -> {
            if (isValidSize(size, sizeType)) {
                ProductSize productSize = ProductSize.builder()
                        .size(size)
                        .purchasePrice(releasePrice) // 출시가를 구매가로 설정
                        .salePrice(releasePrice)    // 출시가를 판매가로 설정
                        .quantity(0)               // 기본 재고 수량은 0
                        .productColor(productColor)
                        .build();
                productSizeRepository.save(productSize);
            } else {
                throw new IllegalArgumentException("유효하지 않은 사이즈입니다: " + size);
            }
        });
    }

    private Category findRootCategory(Category category) {
        while (category.getParentCategory() != null) {
            category = category.getParentCategory();
        }
        return category;
    }

    private SizeType determineSizeType(String rootCategoryName) {
        switch (rootCategoryName.toUpperCase()) {
            case "CLOTHING":
                return SizeType.CLOTHING;
            case "SHOES":
                return SizeType.SHOES;
            case "ACCESSORIES":
                return SizeType.ACCESSORIES;
            default:
                throw new IllegalArgumentException("해당 카테고리에 맞는 SizeType이 존재하지 않습니다.");
        }
    }

    private boolean isValidSize(String size, SizeType sizeType) {
        return Arrays.asList(sizeType.getSizes()).contains(size);
    }
}