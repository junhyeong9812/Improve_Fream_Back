package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderProduct;
import Fream_back.improve_Fream_Back.order.repository.OrderProductRepository;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
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
public class OrderProductRepositoryTest {

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserProductRepository userProductRepository;

    @Test
    @DisplayName("OrderProduct 저장 및 연관 데이터 조회 테스트")
    public void saveAndFindOrderProduct() {
        // User 및 Product 생성 후 저장
        User user = User.builder()
                .loginId("testuser")
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

        // Order 생성 및 저장
        Order order = Order.builder()
                .user(user)
                .status("주문완료")
                .build();
        orderRepository.save(order);

        // OrderProduct 생성 및 저장
        OrderProduct orderProduct = OrderProduct.builder()
                .order(order)
                .userProduct(userProduct)
                .quantity(3)
                .build();
        orderProductRepository.save(orderProduct);

        // OrderProduct 및 연관 데이터 조회
        OrderProduct foundOrderProduct = orderProductRepository.findById(orderProduct.getId()).orElseThrow();
        assertThat(foundOrderProduct.getOrder().getStatus()).isEqualTo("주문완료");
        assertThat(foundOrderProduct.getUserProduct().getProduct().getName()).isEqualTo("Sample Product");
        assertThat(foundOrderProduct.getQuantity()).isEqualTo(3);
    }
}

