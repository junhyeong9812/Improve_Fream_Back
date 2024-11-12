package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.entity.Role;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("Order 저장 및 연관 데이터 조회 테스트")
    public void saveAndFindOrder() {
        // User 및 Delivery 생성 후 저장
        User user = User.builder()
                .username("testuser")
                .password("password")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Delivery delivery = Delivery.builder()
                .recipientName("Recipient")
                .phoneNumber("123-4567")
                .address("123 Main St")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .isDefault(true)
                .build();
        deliveryRepository.save(delivery);

        // Order 생성 및 저장
        Order order = Order.builder()
                .user(user)
                .delivery(delivery)
                .status("주문완료")
                .build();
        orderRepository.save(order);

        // Order 및 연관 데이터 조회
        Order foundOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(foundOrder.getStatus()).isEqualTo("주문완료");
        assertThat(foundOrder.getUser().getUsername()).isEqualTo("testuser");
        assertThat(foundOrder.getDelivery().getRecipientName()).isEqualTo("Recipient");
    }
}

