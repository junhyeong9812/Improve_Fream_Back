package Fream_back.improve_Fream_Back.shipment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class Shipment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String receiverName; // 수령인 이름
    private String receiverPhone; // 수령인 전화번호
    private String postalCode; // 우편번호
    private String address; // 주소
    private String courier; // 택배사
    private String trackingNumber; // 송장 번호

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status; // 배송 상태 (배송시작, 배송중, 배송완료 등)

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void updateTrackingInfo(String courier, String trackingNumber) {
        this.courier = courier;
        this.trackingNumber = trackingNumber;
        this.status = ShipmentStatus.IN_TRANSIT; // 배송중 상태로 변경
    }
    public void updateStatus(ShipmentStatus newStatus) {
        if (this.status == null || this.status.canTransitionTo(newStatus)) {
            this.status = newStatus;
        } else {
            throw new IllegalStateException(
                    "Cannot transition from " + this.status + " to " + newStatus
            );
        }
    }
}
