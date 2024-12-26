package Fream_back.improve_Fream_Back.shipment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
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
    @JoinColumn(name = "order_id", nullable = true) // 구매와 연결 (nullable)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = true) // 판매와 연결 (nullable)
    private Sale sale;

    private String receiverName; // 수령인 이름
    private String receiverPhone; // 수령인 전화번호
    private String postalCode; // 우편번호
    private String address; // 주소
    private String courier; // 택배사
    private String trackingNumber; // 송장 번호

    private boolean isReturnShipment = false; // 반송 여부 플래그

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status; // 배송 상태 (배송 시작, 배송 중, 배송 완료 등)

    // ====== 연관 관계 설정 ======

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void assignSale(Sale sale) {
        this.sale = sale;
    }

    public void markAsReturnShipment() {
        this.isReturnShipment = true;
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
