package Fream_back.improve_Fream_Back.event.repository;

import Fream_back.improve_Fream_Back.event.entity.Event;
import Fream_back.improve_Fream_Back.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByBrand(Brand brand); // 특정 브랜드의 이벤트 조회
    List<Event> findByStartDateBeforeAndEndDateAfter(LocalDateTime startDate, LocalDateTime endDate); // 활성 이벤트 조회
}