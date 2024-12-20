package Fream_back.improve_Fream_Back.product.service.productSize;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.repository.ProductSizeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestProductConfig.class)
@Transactional
class ProductSizeCommandServiceTest {

    @Autowired
    private ProductSizeCommandService productSizeCommandService;

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private TestProductConfig.TestData testData;

    @Test
    @DisplayName("상품 색상에 대한 새로운 사이즈 추가 테스트")
    void createProductSizes() {
        // Given
        ProductColor productColor = testData.getProducts().get(0).getColors().get(0); // 첫 번째 상품의 첫 번째 색상
        Category category = productColor.getProduct().getCategory();
        List<String> newSizes = List.of("270", "280", "290");
        int releasePrice = productColor.getProduct().getReleasePrice();

        // When
        productSizeCommandService.createProductSizes(productColor, category.getId(), newSizes, releasePrice);

        // Then
        List<ProductSize> sizes = productSizeRepository.findAllByProductColorId(productColor.getId());
        assertThat(sizes).hasSize(6); // 기존 3개 + 새로 추가된 3개
        assertThat(sizes.stream().map(ProductSize::getSize)).containsAll(newSizes);
    }

    @Test
    @DisplayName("유효하지 않은 사이즈 추가 시 예외 발생 테스트")
    void createInvalidProductSizes() {
        // Given
        ProductColor productColor = testData.getProducts().get(0).getColors().get(0);
        Category category = productColor.getProduct().getCategory();
        List<String> invalidSizes = List.of("500", "600"); // 유효하지 않은 사이즈
        int releasePrice = productColor.getProduct().getReleasePrice();

        // When & Then
        assertThatThrownBy(() ->
                productSizeCommandService.createProductSizes(productColor, category.getId(), invalidSizes, releasePrice)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 사이즈");
    }

    @Test
    @DisplayName("상품 사이즈 업데이트 테스트")
    void updateProductSize() {
        // Given
        ProductSize productSize = productSizeRepository.findAll().get(0); // 첫 번째 사이즈
        int newPurchasePrice = 200;
        int newSalePrice = 250;
        int newQuantity = 50;

        // When
        productSizeCommandService.updateProductSize(productSize.getId(), newPurchasePrice, newSalePrice, newQuantity);

        // Then
        ProductSize updatedSize = productSizeRepository.findById(productSize.getId()).orElseThrow();
        assertThat(updatedSize.getPurchasePrice()).isEqualTo(newPurchasePrice);
        assertThat(updatedSize.getSalePrice()).isEqualTo(newSalePrice);
        assertThat(updatedSize.getQuantity()).isEqualTo(newQuantity);
    }

    @Test
    @DisplayName("상품 사이즈 삭제 테스트")
    void deleteProductSize() {
        // Given
        ProductSize productSize = productSizeRepository.findAll().get(0);

        // When
        productSizeCommandService.deleteProductSize(productSize.getId());

        // Then
        assertThat(productSizeRepository.findById(productSize.getId())).isEmpty();
    }

    @Test
    @DisplayName("상품 색상에 연관된 모든 사이즈 삭제 테스트")
    void deleteAllSizesByProductColor() {
        // Given
        ProductColor productColor = testData.getProducts().get(0).getColors().get(0); // 첫 번째 상품의 첫 번째 색상

        // When
        productSizeCommandService.deleteAllSizesByProductColor(productColor);

        // Then
        List<ProductSize> sizes = productSizeRepository.findAllByProductColorId(productColor.getId());
        assertThat(sizes).isEmpty();
    }
}
