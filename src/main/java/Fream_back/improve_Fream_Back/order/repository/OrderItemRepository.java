package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId); // 특정 주문의 아이템 조회
    List<OrderItem> findByProductSize(ProductSize productSize); // 특정 제품 사이즈의 아이템 조회
    List<OrderItem> findByQuantityGreaterThan(int quantity); // 특정 수량 이상의 아이템 조회
}
