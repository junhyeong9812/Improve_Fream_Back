package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
    List<ProductColor> findByProductId(Long productId);
}
