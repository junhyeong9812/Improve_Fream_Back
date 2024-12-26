package Fream_back.improve_Fream_Back.order.entity;

import Fream_back.improve_Fream_Back.WarehouseStorage.entity.WarehouseStorage;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment; // 연관된 결제 정보

    private int totalAmount; // 총 금액
    private int discountAmount; // 할인 금액
    private int usedPoints; // 사용된 포인트

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 (결제대기, 결제완료 등)

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Shipment shipment; // 배송 정보

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private WarehouseStorage warehouseStorage; // 창고 보관 정보

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void assignPayment(Payment payment) {
        this.payment = payment;
        payment.assignOrder(this);
    }

    public void assignShipment(Shipment shipment) {
        this.shipment = shipment;
        shipment.assignOrder(this);
    }

    public void assignWarehouseStorage(WarehouseStorage warehouseStorage) {
        this.warehouseStorage = warehouseStorage;
        warehouseStorage.assignOrder(this);
    }

    public void updateStatus(OrderStatus newStatus) {
        if (this.status.canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException("상태 전환이 허용되지 않습니다: " + this.status + " -> " + newStatus);
        }
    }
}
