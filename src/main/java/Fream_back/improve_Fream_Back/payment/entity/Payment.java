package Fream_back.improve_Fream_Back.payment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type")
public abstract class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale; // 판매와 연결

    @Column(nullable = false)
    private double paidAmount; // 결제 금액
    private boolean isSuccess; // 성공 여부
    private LocalDateTime paymentDate; // 결제 완료 시간

    public void assignOrder(Order order) {
        this.order = order;
        this.sale = null; // 서로 배타적 관계
    }

    public void assignSale(Sale sale) {
        this.sale = sale;
        this.order = null; // 서로 배타적 관계
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
        this.isSuccess = paidAmount > 0; // 결제 금액이 양수면 성공으로 간주
    }
}
