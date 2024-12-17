package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProductColorId(Long productColorId);
}
