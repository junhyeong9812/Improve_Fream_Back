package Fream_back.improve_Fream_Back.shipment.repository;

import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrder_User_EmailAndStatus(String email, ShipmentStatus status);
}
