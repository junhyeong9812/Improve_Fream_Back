package Fream_back.improve_Fream_Back.shipment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Shipment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 배송 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 연결된 주문

    private String trackingNumber; // 운송장 번호
    private String courierCompany; // 택배사

    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus; // 배송 상태 (준비중, 배송중, 배송완료 등)

    private LocalDate shippedAt; // 발송일
    private LocalDate deliveredAt; // 배송 완료일

    // 연관관계 편의 메서드
    public void assignOrder(Order order) {
        this.order = order;
    }

    public void updateShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
        // 배송 완료 시점 처리
        if (shipmentStatus == ShipmentStatus.DELIVERED) {
            markAsDelivered();
        }
    }

    public void registerTrackingInfo(String trackingNumber, String courierCompany) {
        this.trackingNumber = trackingNumber;
        this.courierCompany = courierCompany;
    }
    // 배송 완료 처리
    private void markAsDelivered() {
        this.deliveredAt = LocalDate.now();
    }
}