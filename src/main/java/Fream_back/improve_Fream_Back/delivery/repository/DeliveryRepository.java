package Fream_back.improve_Fream_Back.delivery.repository;

import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("SELECT d FROM Delivery d JOIN FETCH d.user WHERE d.user.id = :userId")
    List<Delivery> findAllByUserIdWithFetchJoin(@Param("userId") Long userId);

    long countByUserId(Long userId);

    List<Delivery> findAllByUserId(Long userId);
}
