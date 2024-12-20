package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class)
class ProductImageRepositoryTest {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductDetailImageRepository productDetailImageRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Test
    @DisplayName("ProductImage 저장 및 조회 테스트")
    void saveAndFindProductImage() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Red")
                .content("Detailed content about the color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductImage productImage = ProductImage.builder()
                .imageUrl("http://example.com/image1.jpg")
                .productColor(savedColor)
                .build();

        // When
        ProductImage savedImage = productImageRepository.save(productImage);

        // Then
        List<ProductImage> result = productImageRepository.findByProductColorId(savedColor.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageUrl()).isEqualTo("http://example.com/image1.jpg");
    }

    @Test
    @DisplayName("ProductDetailImage 저장 및 조회 테스트")
    void saveAndFindProductDetailImage() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Blue")
                .content("Detailed content about the blue color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductDetailImage detailImage = ProductDetailImage.builder()
                .imageUrl("http://example.com/detail1.jpg")
                .productColor(savedColor)
                .build();

        // When
        ProductDetailImage savedDetailImage = productDetailImageRepository.save(detailImage);

        // Then
        List<ProductDetailImage> result = productDetailImageRepository.findByProductColorId(savedColor.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageUrl()).isEqualTo("http://example.com/detail1.jpg");
    }

    @Test
    @DisplayName("ProductImage 삭제 테스트")
    void deleteProductImage() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Green")
                .content("Content about green color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductImage productImage = ProductImage.builder()
                .imageUrl("http://example.com/image-to-delete.jpg")
                .productColor(savedColor)
                .build();
        ProductImage savedImage = productImageRepository.save(productImage);

        // When
        productImageRepository.delete(savedImage);

        // Then
        List<ProductImage> result = productImageRepository.findByProductColorId(savedColor.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ProductDetailImage 삭제 테스트")
    void deleteProductDetailImage() {
        // Given
        ProductColor productColor = ProductColor.builder()
                .colorName("Yellow")
                .content("Content about yellow color")
                .build();
        ProductColor savedColor = productColorRepository.save(productColor);

        ProductDetailImage detailImage = ProductDetailImage.builder()
                .imageUrl("http://example.com/detail-to-delete.jpg")
                .productColor(savedColor)
                .build();
        ProductDetailImage savedDetailImage = productDetailImageRepository.save(detailImage);

        // When
        productDetailImageRepository.delete(savedDetailImage);

        // Then
        List<ProductDetailImage> result = productDetailImageRepository.findByProductColorId(savedColor.getId());
        assertThat(result).isEmpty();
    }
}
