package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
