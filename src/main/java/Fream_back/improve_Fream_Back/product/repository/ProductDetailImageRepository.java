package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDetailImageRepository extends JpaRepository<ProductDetailImage, Long> {
    List<ProductDetailImage> findByProductColorId(Long productColorId);
}
