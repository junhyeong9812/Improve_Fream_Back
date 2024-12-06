package Fream_back.improve_Fream_Back.shipment.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ShipmentRepositoryTest {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 배송_생성_및_조회() {
        // Given
        Order order = createOrder();
        Shipment shipment = Shipment.builder()
                .order(order)
                .trackingNumber("123456789")
                .courierCompany("UPS")
                .shipmentStatus(ShipmentStatus.SHIPPED)
                .build();
        shipmentRepository.save(shipment);

        // When
        Optional<Shipment> foundShipment = shipmentRepository.findByOrderId(order.getId());

        // Then
        assertTrue(foundShipment.isPresent());
        assertEquals("UPS", foundShipment.get().getCourierCompany());
        assertEquals(ShipmentStatus.SHIPPED, foundShipment.get().getShipmentStatus());
    }


    private Order createOrder() {
        User user = User.builder()
                .loginId("user")
                .password("password")
                .nickname("nickname")
                .build();
        userRepository.save(user);

        Order order = Order.builder()
                .user(user)
                .recipientName("Recipient")
                .address("Address")
                .totalPrice(BigDecimal.valueOf(300.0))
                .build();
        orderRepository.save(order);
        return order;
    }
}