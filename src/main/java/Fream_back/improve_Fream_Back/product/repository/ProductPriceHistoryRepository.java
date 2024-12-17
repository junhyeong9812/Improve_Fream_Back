package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.ProductPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {
    List<ProductPriceHistory> findByProductSizeId(Long productSizeId);
}