package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}