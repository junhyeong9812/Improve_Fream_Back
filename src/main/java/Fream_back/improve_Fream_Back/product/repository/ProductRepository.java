package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.mainCategory " +
            "LEFT JOIN FETCH p.subCategory " +
            "LEFT JOIN FETCH p.sizeAndColorQuantities " +
            "LEFT JOIN FETCH p.userProducts " +
            "WHERE p.id = :id")
    Product findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.mainCategory " +
            "LEFT JOIN FETCH p.subCategory " +
            "LEFT JOIN FETCH p.sizeAndColorQuantities " +
            "LEFT JOIN FETCH p.userProducts")
    List<Product> findAllWithDetails();

}
