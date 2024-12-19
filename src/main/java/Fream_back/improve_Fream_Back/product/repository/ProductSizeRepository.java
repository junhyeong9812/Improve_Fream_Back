package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProductColorId(Long productColorId);
    Optional<ProductSize> findByProductColorIdAndSize(Long productColorId, String size);
    // 특정 ProductColor에 속한 모든 ProductSize 조회
    List<ProductSize> findAllByProductColorId(Long productColorId);
}
