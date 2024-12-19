package Fream_back.improve_Fream_Back.product.service.productImage;

import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import Fream_back.improve_Fream_Back.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageCommandService {
    private final ProductImageRepository productImageRepository;

    public ProductImage createProductImage(String imageUrl, ProductColor productColor) {
        ProductImage productImage = ProductImage.builder()
                .imageUrl(imageUrl)
                .productColor(productColor)
                .build();
        return productImageRepository.save(productImage);
    }

    public void deleteProductImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지를 찾을 수 없습니다."));
        productImageRepository.delete(productImage);
    }
}