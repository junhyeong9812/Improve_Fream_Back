package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.SizeType;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class) // QueryDSL 설정 필요 시 추가
class ProductSizeRepositoryTest {

    @Autowired
    private ProductSizeRepository productSizeRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Test
    @DisplayName("ProductSize 저장 및 조회 테스트")
    void saveAndFindProductSize() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Red")
                .content("Detailed content about red color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductSize productSize = ProductSize.builder()
                .sizeType(SizeType.SHOES)
                .size("250")
                .purchasePrice(100)
                .salePrice(120)
                .quantity(10)
                .productColor(savedColor)
                .build();

        // When
        ProductSize savedSize = productSizeRepository.save(productSize);

        // Then
        List<ProductSize> result = productSizeRepository.findByProductColorId(savedColor.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSize()).isEqualTo("250");
        assertThat(result.get(0).getPurchasePrice()).isEqualTo(100);
        assertThat(result.get(0).getSalePrice()).isEqualTo(120);
    }

    @Test
    @DisplayName("ProductSize 업데이트 테스트")
    void updateProductSize() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Blue")
                .content("Detailed content about blue color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductSize productSize = ProductSize.builder()
                .sizeType(SizeType.CLOTHING)
                .size("M")
                .purchasePrice(80)
                .salePrice(100)
                .quantity(20)
                .productColor(savedColor)
                .build();
        ProductSize savedSize = productSizeRepository.save(productSize);

        // When
        savedSize.update(90, 110, 15);
        productSizeRepository.save(savedSize);

        // Then
        Optional<ProductSize> result = productSizeRepository.findById(savedSize.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getPurchasePrice()).isEqualTo(90);
        assertThat(result.get().getSalePrice()).isEqualTo(110);
        assertThat(result.get().getQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("ProductSize 삭제 테스트")
    void deleteProductSize() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Green")
                .content("Detailed content about green color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductSize productSize = ProductSize.builder()
                .sizeType(SizeType.ACCESSORIES)
                .size("L")
                .purchasePrice(50)
                .salePrice(70)
                .quantity(5)
                .productColor(savedColor)
                .build();
        ProductSize savedSize = productSizeRepository.save(productSize);

        // When
        productSizeRepository.delete(savedSize);

        // Then
        List<ProductSize> result = productSizeRepository.findByProductColorId(savedColor.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ProductColorId와 Size로 ProductSize 조회 테스트")
    void findByProductColorIdAndSize() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Yellow")
                .content("Detailed content about yellow color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductSize productSize = ProductSize.builder()
                .sizeType(SizeType.CLOTHING)
                .size("XL")
                .purchasePrice(120)
                .salePrice(150)
                .quantity(8)
                .productColor(savedColor)
                .build();
        productSizeRepository.save(productSize);

        // When
        Optional<ProductSize> result = productSizeRepository.findByProductColorIdAndSize(savedColor.getId(), "XL");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSize()).isEqualTo("XL");
        assertThat(result.get().getPurchasePrice()).isEqualTo(120);
    }
}
