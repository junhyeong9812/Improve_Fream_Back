package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Interest;
import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    boolean existsByUserAndProductColor(User user, ProductColor productColor);

    Optional<Interest> findByUserAndProductColor(User user, ProductColor productColor);

    List<Interest> findAllByUserId(Long userId);
}