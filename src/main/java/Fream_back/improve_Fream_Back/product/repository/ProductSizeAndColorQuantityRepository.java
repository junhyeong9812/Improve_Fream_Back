package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSizeAndColorQuantityRepository extends JpaRepository<ProductSizeAndColorQuantity, Long> {
    List<ProductSizeAndColorQuantity> findAllByProductId(Long productId);
}