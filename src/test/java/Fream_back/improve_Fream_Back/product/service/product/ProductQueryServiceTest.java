package Fream_back.improve_Fream_Back.product.service.product;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.ProductDetailResponseDto;
import Fream_back.improve_Fream_Back.product.dto.ProductSearchResponseDto;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestProductConfig.class)
class ProductQueryServiceTest {

    @Autowired
    private ProductQueryService productQueryService;

    @Autowired
    private TestProductConfig.TestData testData;

    @Test
    @DisplayName("상품 검색 테스트")
    void searchProducts() {
        // Given
        String keyword = "Product";
        List<Long> categoryIds = List.of(testData.getProducts().get(0).getCategory().getId());
        List<GenderType> genders = List.of(GenderType.UNISEX);
        List<Long> brandIds = List.of(testData.getProducts().get(0).getBrand().getId());
        List<Long> collectionIds = List.of(testData.getProducts().get(0).getCollection().getId());
        List<String> colors = List.of(testData.getProducts().get(0).getColors().get(0).getColorName());
        List<String> sizes = testData.getProducts().get(0).getColors().get(0).getSizes()
                .stream()
                .map(ProductSize::getSize)
                .collect(Collectors.toList());
        Integer minPrice = 100;
        Integer maxPrice = 500;
        SortOption sortOption = new SortOption("price", "asc");
        PageRequest pageable = PageRequest.of(0, 10);

        System.out.println("Generated Test Data:");
        testData.getProducts().forEach(product -> {
            System.out.println("Product ID: " + product.getId());
            System.out.println("  Name: " + product.getName());
            System.out.println("  English Name: " + product.getEnglishName());
            System.out.println("  Category ID: " + product.getCategory().getId());
            System.out.println("  Gender: " + product.getGender());
            System.out.println("  Brand ID: " + product.getBrand().getId());
            System.out.println("  Collection ID: " + product.getCollection().getId());
            product.getColors().forEach(color -> {
                System.out.println("    Color Name: " + color.getColorName());
                color.getSizes().forEach(size -> {
                    System.out.println("      Size: " + size.getSize());
                    System.out.println("      Purchase Price: " + size.getPurchasePrice());
                });
            });
        });


        // When
        Page<ProductSearchResponseDto> result = productQueryService.searchProducts(
                keyword, categoryIds, genders, brandIds, collectionIds, colors, sizes, minPrice, maxPrice, sortOption, pageable
        );

        // Then
        System.out.println("result = " + result);
        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).contains(keyword);
    }

    @Test
    @DisplayName("상품 상세 조회 테스트")
    void getProductDetail() {
        // Given
        Long productId = testData.getProducts().get(0).getId();
        String colorName = testData.getProducts().get(0).getColors().get(0).getColorName();

        // When
        ProductDetailResponseDto result = productQueryService.getProductDetail(productId, colorName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getColorName()).isEqualTo(colorName);
        assertThat(result.getOtherColors()).isNotEmpty();
        assertThat(result.getSizes()).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 상품 상세 조회 시 예외 발생")
    void getProductDetailNotFound() {
        // Given
        Long invalidProductId = -1L;
        String invalidColorName = "InvalidColor";

        // When & Then
        assertThatThrownBy(() -> productQueryService.getProductDetail(invalidProductId, invalidColorName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 상품 또는 색상이 존재하지 않습니다.");
    }
}
