//package Fream_back.improve_Fream_Back.order.entity;
//
//import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
//import Fream_back.improve_Fream_Back.product.entity.Product;
//import Fream_back.improve_Fream_Back.style.entity.Style;
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@EntityListeners(AuditingEntityListener.class)
//public class OrderItem extends BaseTimeEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id; // 주문 상품 ID
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id")
//    private Order order; // 연결된 주문
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product; // 연결된 상품
//
//    private int quantity; // 주문 수량
//    private BigDecimal price; // 주문 당시의 상품 가격
//
////    @Builder.Default
////    private boolean reviewWritten = false; // 리뷰 작성 여부
//
//    @Builder.Default
//    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Style> styles = new ArrayList<>(); // 작성된 스타일 목록
//
//    // 연관관계 편의 메서드
//    public void assignOrder(Order order) {
//        this.order = order;
//    }
//    public BigDecimal getTotalPrice() {
//        return price.multiply(BigDecimal.valueOf(quantity));
//    }
//    public void addStyle(Style style) {
//        this.styles.add(style);
//        style.assignOrderItem(this);
//    }
////    // 리뷰 작성 메서드
////    public void markReviewWritten() {
////        if (!order.isPaymentCompleted()) {
////            throw new IllegalStateException("결제가 완료되지 않은 주문에 대해서는 리뷰를 작성할 수 없습니다.");
////        }
////        this.reviewWritten = true;
////    }
//}