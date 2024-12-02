package Fream_back.improve_Fream_Back.payment.repository;

import Fream_back.improve_Fream_Back.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 주문 ID를 기반으로 결제 내역 조회
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    List<Payment> findByOrderId(@Param("orderId") Long orderId);

    // 특정 주문의 결제 성공 여부 확인
    @Query("SELECT p.isSuccessful FROM Payment p WHERE p.order.id = :orderId")
    boolean isPaymentSuccessful(@Param("orderId") Long orderId);

    // 주문 ID를 기반으로 단일 결제 조회 (결제가 단일일 경우 사용)
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    Optional<Payment> findSinglePaymentByOrderId(@Param("orderId") Long orderId);
}
