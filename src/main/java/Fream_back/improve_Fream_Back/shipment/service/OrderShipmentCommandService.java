package Fream_back.improve_Fream_Back.shipment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
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
}

