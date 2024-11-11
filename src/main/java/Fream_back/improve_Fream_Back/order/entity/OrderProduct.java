package Fream_back.improve_Fream_Back.order.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.product.entity.UserProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * OrderProduct
 *
 * 주문과 상품 간의 다대다 관계를 풀기 위한 중간 엔티티입니다.
 * 각 주문에 포함된 개별 상품 정보를 관리합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderProduct extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 상품 ID (기본 키)

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // 어떤 주문에 속해 있는지

    @ManyToOne
    @JoinColumn(name = "user_product_id")
    private UserProduct userProduct; // 어떤 상품을 주문했는지

    private int quantity; // 주문한 수량

    // 연관관계 편의 메서드 - Order 지정
    public void assignOrder(Order order) {
        this.order = order;
    }

    // 연관관계 편의 메서드 - UserProduct 지정
    public void assignUserProduct(UserProduct userProduct) {
        this.userProduct = userProduct;
    }
}
