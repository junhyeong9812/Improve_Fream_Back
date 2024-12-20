package Fream_back.improve_Fream_Back.product.service.product;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.ProductCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductUpdateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductUpdateResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestProductConfig.class)
@Transactional
class ProductCommandServiceTest {

    @Autowired
    private ProductCommandService productCommandService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestProductConfig.TestData testData;

    @Test
    @DisplayName("상품 생성 테스트")
    void createProduct() {
        // Given
        ProductCreateRequestDto request = ProductCreateRequestDto.builder()
                .name("New Product")
                .englishName("New Product English")
                .releasePrice(300)
                .modelNumber("Model-123")
                .releaseDate("2023-12-01")
                .gender(GenderType.UNISEX)
                .brandName("Nike")
                .mainCategoryName("Shoes")
                .categoryName("Sneakers")
                .collectionName("Jordan")
                .build();

        // When
        var response = productCommandService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Product");
        assertThat(response.getBrandName()).isEqualTo("Nike");
        assertThat(response.getCategoryName()).isEqualTo("Sneakers");
    }

    @Test
    @DisplayName("상품 업데이트 테스트")
    void updateProduct() {
        // Given
        Product existingProduct = testData.getProducts().get(0); // 기존 상품
        ProductUpdateRequestDto request = ProductUpdateRequestDto.builder()
                .name("Updated Product")
                .englishName("Updated English Name")
                .releasePrice(350)
                .modelNumber("Updated-Model")
                .releaseDate("2023-12-15")
                .gender(GenderType.UNISEX)
                .brandName("Adidas")
                .mainCategoryName("Shoes")
                .categoryName("Boots")
                .collectionName("Updated Collection")
                .build();

        // When
        ProductUpdateResponseDto response = productCommandService.updateProduct(existingProduct.getId(), request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Product");
        assertThat(response.getBrandName()).isEqualTo("Adidas");
        assertThat(response.getCategoryName()).isEqualTo("Boots");
    }

    @Test
    @DisplayName("존재하지 않는 상품 업데이트 시 예외 발생")
    void updateNonExistingProduct() {
        // Given
        Long invalidProductId = -1L; // 잘못된 ID
        ProductUpdateRequestDto request = ProductUpdateRequestDto.builder()
                .name("Invalid Update")
                .build();

        // When & Then
        assertThatThrownBy(() -> productCommandService.updateProduct(invalidProductId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void deleteProduct() {
        // Given
        Product productToDelete = testData.getProducts().get(0);

        // When
        productCommandService.deleteProduct(productToDelete.getId());

        // Then
        assertThat(productRepository.findById(productToDelete.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제 시 예외 발생")
    void deleteNonExistingProduct() {
        // Given
        Long invalidProductId = -1L;

        // When & Then
        assertThatThrownBy(() -> productCommandService.deleteProduct(invalidProductId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품입니다.");
    }
}
