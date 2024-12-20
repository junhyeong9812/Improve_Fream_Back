package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.entity.Collection;
import Fream_back.improve_Fream_Back.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 연관된 Product 존재 여부 확인
    boolean existsByBrand(Brand brand);
    boolean existsByCategory(Category category); // 새로운 메서드 추가
    boolean existsByCollection(Collection collection);
}