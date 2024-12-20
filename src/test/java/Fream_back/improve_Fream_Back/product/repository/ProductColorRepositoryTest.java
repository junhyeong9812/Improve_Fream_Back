package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
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
class ProductColorRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Test
    @DisplayName("상품 색상 저장 및 조회 테스트")
    void saveAndFindProductColor() {
        // Given
        Product product = Product.builder()
                .name("Jordan")
                .englishName("Air Jordan")
                .releasePrice(200)
                .modelNumber("AJ2023")
                .releaseDate("2023-05-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor color = ProductColor.builder()
                .colorName("Red")
                .product(savedProduct)
                .build();
        ProductColor savedColor = productColorRepository.save(color);

        // When
        Optional<ProductColor> result = productColorRepository.findById(savedColor.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getColorName()).isEqualTo("Red");
        assertThat(result.get().getProduct().getId()).isEqualTo(savedProduct.getId());
    }

    @Test
    @DisplayName("상품 ID로 색상 조회 테스트")
    void findByProductId() {
        // Given
        Product product = Product.builder()
                .name("Jordan")
                .englishName("Air Jordan")
                .releasePrice(200)
                .modelNumber("AJ2023")
                .releaseDate("2023-05-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor redColor = ProductColor.builder()
                .colorName("Red")
                .product(savedProduct)
                .build();
        ProductColor blueColor = ProductColor.builder()
                .colorName("Blue")
                .product(savedProduct)
                .build();

        productColorRepository.save(redColor);
        productColorRepository.save(blueColor);

        // When
        List<ProductColor> colors = productColorRepository.findByProductId(savedProduct.getId());

        // Then
        assertThat(colors).hasSize(2);
        assertThat(colors).extracting(ProductColor::getColorName)
                .containsExactlyInAnyOrder("Red", "Blue");
    }

    @Test
    @DisplayName("상품 색상 삭제 테스트")
    void deleteProductColor() {
        // Given
        Product product = Product.builder()
                .name("Jordan")
                .englishName("Air Jordan")
                .releasePrice(200)
                .modelNumber("AJ2023")
                .releaseDate("2023-05-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor color = ProductColor.builder()
                .colorName("Green")
                .product(savedProduct)
                .build();
        ProductColor savedColor = productColorRepository.save(color);

        // When
        productColorRepository.delete(savedColor);

        // Then
        Optional<ProductColor> result = productColorRepository.findById(savedColor.getId());
        assertThat(result).isEmpty();
    }
    @Test
    @DisplayName("ProductColor 업데이트 테스트")
    void updateProductColor() {
        // Given
        Product product = Product.builder()
                .name("Jordan")
                .englishName("Air Jordan")
                .releasePrice(200)
                .modelNumber("AJ2023")
                .releaseDate("2023-05-01")
                .gender(GenderType.UNISEX)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductColor color = ProductColor.builder()
                .colorName("Red")
                .content("Initial Content")
                .product(savedProduct)
                .build();
        ProductColor savedColor = productColorRepository.save(color);

        // When
        savedColor.update("Blue", "Updated Content");
        ProductColor updatedColor = productColorRepository.save(savedColor);

        // Then
        Optional<ProductColor> result = productColorRepository.findById(updatedColor.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getColorName()).isEqualTo("Blue");
        assertThat(result.get().getContent()).isEqualTo("Updated Content");
    }
}
