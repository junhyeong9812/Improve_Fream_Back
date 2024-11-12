package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
