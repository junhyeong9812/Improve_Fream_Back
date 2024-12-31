package Fream_back.improve_Fream_Back.sale.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleBid extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller; // 판매자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize; // 판매 대상 상품 사이즈

    private int bidPrice; // 판매자가 원하는 가격

    @Enumerated(EnumType.STRING)
    private BidStatus status = BidStatus.PENDING; // 입찰 상태 (대기 중, 매칭 완료 등)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale; // 매칭된 판매 엔티티

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 매칭된 구매 엔티티


    // 즉시 판매 플래그
    @Column(nullable = false)
    private boolean isInstantSale = false;

    public void assignSale(Sale sale) {
        this.sale = sale;
        this.status = BidStatus.MATCHED;
    }

    public void assignOrder(Order order) {
        this.order = order;
        this.status = BidStatus.MATCHED;
    }
    public void markAsInstantSale() {
        this.isInstantSale = true;
    }

    public void updateStatus(BidStatus newStatus) {
        this.status = newStatus;
    }
}
