package Fream_back.improve_Fream_Back.shipment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderStatus;
import Fream_back.improve_Fream_Back.shipment.entity.OrderShipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.shipment.repository.OrderShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderShipmentCommandService {

    private final OrderShipmentRepository orderShipmentRepository;

    @Transactional
    public OrderShipment createOrderShipment(Order order, String receiverName, String receiverPhone,
                                             String postalCode, String address) {
        OrderShipment shipment = OrderShipment.builder()
                .order(order)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .postalCode(postalCode)
                .address(address)
                .status(ShipmentStatus.PENDING) // 초기 상태 설정
                .build();

        return orderShipmentRepository.save(shipment);
    }
    @Transactional
    public void updateShipmentStatus(OrderShipment shipment, ShipmentStatus newStatus) {
        shipment.updateStatus(newStatus);
        orderShipmentRepository.save(shipment);
    }
    @Transactional
    public void updateTrackingInfo(Long shipmentId, String courier, String trackingNumber) {
        OrderShipment shipment = orderShipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment 정보를 찾을 수 없습니다: " + shipmentId));

        shipment.updateTrackingInfo(courier, trackingNumber);
        shipment.updateStatus(ShipmentStatus.IN_TRANSIT);

        // 배송 상태가 변경되었으므로 주문 상태도 업데이트
        Order order = shipment.getOrder();
        order.updateStatus(OrderStatus.IN_TRANSIT);

        orderShipmentRepository.save(shipment);
    }
}

