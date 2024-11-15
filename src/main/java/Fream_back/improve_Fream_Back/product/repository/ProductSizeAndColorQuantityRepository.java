package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.enumType.ClothingSizeType;
import Fream_back.improve_Fream_Back.product.entity.enumType.Color;
import Fream_back.improve_Fream_Back.product.entity.enumType.ShoeSizeType;
import Fream_back.improve_Fream_Back.product.entity.size.ProductSizeAndColorQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSizeAndColorQuantityRepository extends JpaRepository<ProductSizeAndColorQuantity, Long> {
    List<ProductSizeAndColorQuantity> findAllByProductId(Long productId);
    Optional<ProductSizeAndColorQuantity> findByProductIdAndClothingSizeAndColor(Long productId, ClothingSizeType clothingSize, Color color);
    Optional<ProductSizeAndColorQuantity> findByProductIdAndShoeSizeAndColor(Long productId, ShoeSizeType shoeSize, Color color);
}