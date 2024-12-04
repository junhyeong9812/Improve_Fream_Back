package Fream_back.improve_Fream_Back.order.repository;


import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager em;

    @Test
    void 주문_생성_및_조회() {
        // Given
        User user = createUser();
        Delivery delivery = createDelivery();
        OrderItem orderItem1 = createOrderItem("Product A", "Brand A", "SKU-123", new BigDecimal("100.00"), 2);
        OrderItem orderItem2 = createOrderItem("Product B", "Brand B", "SKU-456", new BigDecimal("200.00"), 1);

        Order order = Order.createOrderFromDelivery(user, delivery, List.of(orderItem1, orderItem2));
        orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findOrderDetailsById(order.getId());

        // Then
        assertTrue(foundOrder.isPresent());
        Order retrievedOrder = foundOrder.get();
        assertEquals(2, retrievedOrder.getOrderItems().size());
        assertEquals("Product A", retrievedOrder.getOrderItems().get(0).getProduct().getName());
    }

    private User createUser() {
        User user = User.builder()
                .loginId("testUser")
                .password("password")
                .nickname("nickname")
                .build();
        em.persist(user);
        return user;
    }

    private Delivery createDelivery() {
        Delivery delivery = Delivery.builder()
                .recipientName("John Doe")
                .phoneNumber("123-456-7890")
                .address("123 Main St")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .build();
        em.persist(delivery);
        return delivery;
    }

    private OrderItem createOrderItem(String productName, String brand, String sku, BigDecimal price, int quantity) {
        Product product = Product.builder()
                .name(productName)
                .brand(brand)
                .sku(sku)
                .initialPrice(price)
                .description("Sample description")
                .releaseDate(LocalDate.now())
                .build();
        em.persist(product); // 영속화

        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
