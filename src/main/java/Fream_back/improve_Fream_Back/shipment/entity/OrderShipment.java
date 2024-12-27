package Fream_back.improve_Fream_Back.shipment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderShipment extends BaseTimeEntity {

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
    private String courier; // 택배사 이름
    private String trackingNumber; // 송장 번호

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;


    public void assignOrder(Order order) {
        this.order = order;
    }

    public void updateTrackingInfo(String courier, String trackingNumber) {
        this.courier = courier;
        this.trackingNumber = trackingNumber;
        this.status = ShipmentStatus.IN_TRANSIT;
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
