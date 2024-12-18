//package Fream_back.improve_Fream_Back.shipment.repository;
//
//import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Optional;
//
//public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
//
//    // 주문 ID를 기반으로 배송 정보 조회
//    @Query("SELECT s FROM Shipment s WHERE s.order.id = :orderId")
//    Optional<Shipment> findByOrderId(@Param("orderId") Long orderId);
//}
