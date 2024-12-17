package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByUserId(Long userId);
    List<Interest> findByProductColorId(Long productColorId);
}