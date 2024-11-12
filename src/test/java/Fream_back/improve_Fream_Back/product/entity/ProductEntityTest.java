package Fream_back.improve_Fream_Back.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ProductEntityTest {

    @Test
    @DisplayName("Product 엔티티 생성 테스트")
    public void createProduct() {
        // Product 엔티티 생성
        Product product = Product.builder()
                .name("Sample Product")
                .brand("Brand A")
                .sku("SKU123")
                .initialPrice(BigDecimal.valueOf(1000))
                .description("Sample Description")
                .build();

        // 데이터 검증
        assertThat(product.getName()).isEqualTo("Sample Product");
        assertThat(product.getBrand()).isEqualTo("Brand A");
        assertThat(product.getSku()).isEqualTo("SKU123");
        assertThat(product.getInitialPrice()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(product.getDescription()).isEqualTo("Sample Description");
    }
}
