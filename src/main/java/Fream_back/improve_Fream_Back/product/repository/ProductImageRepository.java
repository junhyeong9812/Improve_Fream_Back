package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // 특정 ProductColor에 속한 이미지가 존재하는지 확인
    boolean existsByProductColorId(Long productColorId);
    List<ProductImage> findByProductColorId(Long productColorId);
    List<ProductImage> findAllByProductColorId(Long productColorId);
}
