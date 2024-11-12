package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Product 저장 및 조회 테스트")
    public void saveAndFindProduct() {
        // Product 엔티티 생성 및 저장
        Product product = Product.builder()
                .name("Sample Product")
                .brand("Brand A")
                .sku("SKU123")
                .initialPrice(BigDecimal.valueOf(1000))
                .description("Sample Description")
                .build();
        productRepository.save(product);

        // Product 조회
        Optional<Product> foundProduct = productRepository.findById(product.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Sample Product");
    }
}

