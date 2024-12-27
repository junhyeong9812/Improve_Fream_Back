package Fream_back.improve_Fream_Back.shipment.repository;

import Fream_back.improve_Fream_Back.shipment.entity.OrderShipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderShipmentRepository extends JpaRepository<OrderShipment, Long> {
    List<OrderShipment> findByStatusIn(List<ShipmentStatus> statuses);
}

