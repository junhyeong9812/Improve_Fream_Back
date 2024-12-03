package Fream_back.improve_Fream_Back.payment.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.user.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 결제_생성_및_조회() {
        // Given
        Order order = createOrder();
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("Credit Card")
                .amount(new BigDecimal("300.00"))
                .isSuccessful(true)
                .build();
        paymentRepository.save(payment);

        // When
        List<Payment> payments = paymentRepository.findByOrderId(order.getId());

        // Then
        assertEquals(1, payments.size());
        assertEquals("Credit Card", payments.get(0).getPaymentMethod());
    }

    private Order createOrder() {
        User user = User.builder()
                .loginId("user")
                .password("password")
                .nickname("nickname")
                .build();

        Order order = Order.builder()
                .user(user)
                .recipientName("Recipient")
                .address("Address")
                .totalPrice(300.00)
                .build();
        orderRepository.save(order);
        return order;
    }
}