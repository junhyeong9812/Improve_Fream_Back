package Fream_back.improve_Fream_Back.order.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * Order
 *
 * 주문 정보를 관리하는 엔티티입니다.
 * 주문 번호, 사용자 정보, 주문 상태 등을 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 ID (기본 키)

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 주문을 한 사용자

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts; // 주문한 상품 목록

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 주문의 배송 정보

    private String status; // 주문 상태 (e.g., 주문완료, 배송중, 배송완료 등)

    // 연관관계 편의 메서드 - OrderProduct 추가
    public void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.assignOrder(this);
    }
}
