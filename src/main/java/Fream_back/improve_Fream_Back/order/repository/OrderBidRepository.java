package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.entity.OrderBid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderBidRepository extends JpaRepository<OrderBid, Long> {
    Optional<OrderBid> findByOrderId(Long orderId);
}
