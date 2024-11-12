package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.entity.UserProduct;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import Fream_back.improve_Fream_Back.product.repository.UserProductRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserProductRepositoryTest {

    @Autowired
    private UserProductRepository userProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("UserProduct 저장 및 연관 데이터 조회 테스트")
    public void saveAndFindUserProduct() {
        // User 및 Product 생성 후 저장
        User user = User.builder()
                .username("testuser")
                .password("password")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Product product = Product.builder()
                .name("Sample Product")
                .brand("Brand A")
                .sku("SKU123")
                .initialPrice(BigDecimal.valueOf(1000))
                .description("Sample Description")
                .build();
        productRepository.save(product);

        // UserProduct 생성 및 저장
        UserProduct userProduct = UserProduct.builder()
                .product(product)
                .seller(user)
                .sellingPrice(BigDecimal.valueOf(1500))
                .condition("New")
                .quantity(10)
                .isAvailable(true)
                .build();
        userProductRepository.save(userProduct);

        // UserProduct 및 연관 데이터 조회
        UserProduct foundUserProduct = userProductRepository.findById(userProduct.getId()).orElseThrow();
        assertThat(foundUserProduct.getProduct().getName()).isEqualTo("Sample Product");
        assertThat(foundUserProduct.getSeller().getUsername()).isEqualTo("testuser");
    }
}

