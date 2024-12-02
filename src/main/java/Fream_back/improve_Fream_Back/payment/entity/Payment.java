package Fream_back.improve_Fream_Back.payment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결제 ID

//    @ManyToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 연결된 주문

    private String paymentMethod; // 결제 수단 (카드, 계좌이체 등)
    private BigDecimal amount; // 결제 금액
    private LocalDate paymentDate; // 결제 날짜

    private boolean isSuccessful; // 결제 성공 여부
    private boolean isRefunded; // 환불 여부

    // 연관관계 편의 메서드
    public void assignOrder(Order order) {
        this.order = order;
    }

    public void markAsSuccessful() {
        this.isSuccessful = true;
    }
    // 환불 처리
    public void markAsRefunded() {
        if (!this.isSuccessful) {
            throw new IllegalStateException("결제에 성공하지 않은 경우 환불할 수 없습니다.");
        }
        if (this.isRefunded) {
            throw new IllegalStateException("이미 환불된 결제입니다.");
        }
        this.isRefunded = true;
    }
}