package Fream_back.improve_Fream_Back.payment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.payment.dto.PaymentResponseDto;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 Order 생성
        testOrder = Order.builder()
                .id(1L)
                .recipientName("Test User")
                .address("123 Test St")
                .addressDetail("Apt 101")
                .zipCode("12345")
                .paymentCompleted(false)
                .build();
    }

    @Test
    @DisplayName("결제 생성 테스트")
    void createPayment() {
        // Given
        Payment payment = Payment.builder()
                .order(testOrder)
                .paymentMethod("Credit Card")
                .amount(BigDecimal.valueOf(100.0))
                .paymentDate(LocalDate.now())
                .isSuccessful(false)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When
        Payment createdPayment = paymentService.createPayment(testOrder, "Credit Card", BigDecimal.valueOf(100.0));

        // Then
        verify(paymentRepository, times(1)).save(any(Payment.class));
        assertThat(createdPayment.getPaymentMethod()).isEqualTo("Credit Card");
        assertThat(createdPayment.getAmount()).isEqualTo(BigDecimal.valueOf(100.0));
        assertThat(createdPayment.isSuccessful()).isFalse();
    }

    @Test
    @DisplayName("결제 성공 처리 테스트")
    void markPaymentAsSuccessful() {
        // Given
        Long paymentId = 1L;
        Payment payment = Payment.builder()
                .id(paymentId)
                .order(testOrder)
                .isSuccessful(false)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // When
        paymentService.markPaymentAsSuccessful(paymentId);

        // Then
        assertThat(payment.isSuccessful()).isTrue();
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("결제 환불 처리 테스트 - 성공한 결제")
    void refundPayment_successful() {
        // Given
        Long paymentId = 1L;
        Payment payment = Payment.builder()
                .id(paymentId)
                .order(testOrder)
                .isSuccessful(true)
                .isRefunded(false)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // When
        paymentService.refundPayment(paymentId);

        // Then
        assertThat(payment.isRefunded()).isTrue();
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("결제 환불 처리 실패 - 결제가 성공하지 않은 경우")
    void refundPayment_notSuccessful() {
        // Given
        Long paymentId = 1L;
        Payment payment = Payment.builder()
                .id(paymentId)
                .order(testOrder)
                .isSuccessful(false)
                .isRefunded(false)
                .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // When & Then
        assertThrows(IllegalStateException.class, () -> paymentService.refundPayment(paymentId));
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("결제 세부 정보 조회 테스트")
    void getPaymentDetailsByOrder() {
        // Given
        Long orderId = 1L;
        Payment payment = Payment.builder()
                .id(1L)
                .order(testOrder)
                .paymentMethod("Credit Card")
                .amount(BigDecimal.valueOf(100.0))
                .paymentDate(LocalDate.now())
                .isSuccessful(true)
                .build();

        when(paymentRepository.findSinglePaymentByOrderId(orderId)).thenReturn(Optional.of(payment));

        // When
        PaymentResponseDto responseDto = paymentService.getPaymentDetailsByOrder(orderId);

        // Then
        assertThat(responseDto.getPaymentId()).isEqualTo(1L);
        assertThat(responseDto.getPaymentMethod()).isEqualTo("Credit Card");
        assertThat(responseDto.getAmount()).isEqualTo(BigDecimal.valueOf(100.0));
        assertThat(responseDto.isSuccessful()).isTrue();
        verify(paymentRepository, times(1)).findSinglePaymentByOrderId(orderId);
    }

    @Test
    @DisplayName("결제 정보를 찾을 수 없는 경우 예외 처리")
    void paymentNotFound() {
        // Given
        Long paymentId = 999L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> paymentService.markPaymentAsSuccessful(paymentId));
        verify(paymentRepository, times(1)).findById(paymentId);
    }
}