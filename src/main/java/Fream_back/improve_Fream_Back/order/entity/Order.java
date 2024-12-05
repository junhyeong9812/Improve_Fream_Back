package Fream_back.improve_Fream_Back.order.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 주문자

    private String recipientName; // 수령인 이름
    private String phoneNumber; // 수령인 전화번호
    private String address; // 배송지 주소
    private String addressDetail; // 상세 주소
    private String zipCode; // 우편번호

    private boolean paymentCompleted; // 결제 완료 여부

    private BigDecimal totalPrice; // 주문 총 가격

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipment shipment; // 배송 정보

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); // 주문 상품 목록

    //부분 결제 기능 구현 시
//    @Builder.Default
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Payment> payments = new ArrayList<>(); // 결제 내역

    //단일 결제
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment; // 결제 정보 (단일)
    
    
    // 연관관계 편의 메서드
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.assignOrder(this);
        calculateTotalPrice(); // 상품 추가 시 총 가격 재계산
    }
    public void markPaymentCompleted(boolean status) {
        this.paymentCompleted = status;
    }

    //부분 결제 구현 시 사용
//    public void addPayment(Payment payment) {
//        this.payments.add(payment);
//        payment.assignOrder(this);
//    }
    public void assignPayment(Payment payment) {
        this.payment = payment;
        payment.assignOrder(this);
    }

    public void assignShipment(Shipment shipment) {
        this.shipment = shipment;
        shipment.assignOrder(this);
    }

    public void markPaymentCompleted() {
        this.paymentCompleted = true;
    }

    public static Order createOrderFromDelivery(User user, Delivery delivery, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .user(user)
                .recipientName(delivery.getRecipientName())
                .phoneNumber(delivery.getPhoneNumber())
                .address(delivery.getAddress())
                .addressDetail(delivery.getAddressDetail())
                .zipCode(delivery.getZipCode())
                .paymentCompleted(false)
                .totalPrice(BigDecimal.ZERO) // 초기값
                .build();

        // 주문 항목 추가
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    // 총 가격 계산 메서드
    private void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}