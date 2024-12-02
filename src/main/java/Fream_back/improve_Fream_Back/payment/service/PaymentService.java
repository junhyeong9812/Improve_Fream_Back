package Fream_back.improve_Fream_Back.payment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.payment.dto.PaymentResponseDto;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * 결제 생성
     */
    public Payment createPayment(Order order, String paymentMethod, BigDecimal amount) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .amount(amount)
                .paymentDate(LocalDate.now())
                .isSuccessful(false) // 초기값은 실패 상태
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    /**
     * 결제 성공 처리
     */
    @Transactional
    public void markPaymentAsSuccessful(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        payment.markAsSuccessful();
    }

    /**
     * 결제 취소 (환불 처리)
     */
    @Transactional
    public void refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (!payment.isSuccessful()) {
            throw new IllegalStateException("결제에 성공하지 않은 경우 환불할 수 없습니다.");
        }

        // 환불 처리
        payment.markAsRefunded(); // 환불 상태로 변경 (추가된 필드)

        // 주문 결제 완료 상태 해제
        Order order = payment.getOrder();
        if (order != null) {
            order.markPaymentCompleted(false); // 더티 체크로 저장
        }
    }
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentDetailsByOrder(Long orderId) {
        // Order에서 Payment 조회
        Payment payment = paymentRepository.findSinglePaymentByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // PaymentResponseDto로 변환
        return PaymentResponseDto.builder()
                .paymentId(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .isSuccessful(payment.isSuccessful())
                .build();
    }
}
