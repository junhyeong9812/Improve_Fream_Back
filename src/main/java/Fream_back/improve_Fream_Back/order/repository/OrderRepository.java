//package Fream_back.improve_Fream_Back.order.repository;
//
//import Fream_back.improve_Fream_Back.order.entity.Order;
//import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
//
//    // 간단한 주문 목록 조회
//    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
//    List<Order> findSimpleOrdersByUserId(@Param("userId") Long userId);
//
//    // 주문 상세 조회 (패치 조인)
//    @Query("SELECT o FROM Order o " +
//            "LEFT JOIN FETCH o.shipment " +
//            "LEFT JOIN FETCH o.payment " +
//            "LEFT JOIN FETCH o.orderItems oi " +
//            "LEFT JOIN FETCH oi.product " +
//            "WHERE o.id = :orderId")
//    Optional<Order> findOrderDetailsById(@Param("orderId") Long orderId);
//    //환불 대기 상태 주문 조회
//    @Query("SELECT o FROM Order o JOIN o.shipment s WHERE o.user.id = :userId AND s.shipmentStatus = :shipmentStatus")
//    List<Order> findOrdersByUserIdAndShipmentStatus(@Param("userId") Long userId, @Param("shipmentStatus") ShipmentStatus shipmentStatus);
//}