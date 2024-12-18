//package Fream_back.improve_Fream_Back.order.repository;
//
//import Fream_back.improve_Fream_Back.order.entity.OrderItem;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
//
//    // 주문 ID를 기반으로 주문 상품 목록 조회
//    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
//    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
//
//    // 특정 상품의 주문 내역 조회
//    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
//    List<OrderItem> findByProductId(@Param("productId") Long productId);
//
//    //스타일에 따른 상품 주문 내역
//    @Query("SELECT oi FROM OrderItem oi " +
//            "LEFT JOIN FETCH oi.styles " +
//            "WHERE oi.id = :id")
//    Optional<OrderItem> findByIdWithStyles(@Param("id") Long id);
//
//}