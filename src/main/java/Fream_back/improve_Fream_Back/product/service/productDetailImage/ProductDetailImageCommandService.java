package Fream_back.improve_Fream_Back.product.service.productDetailImage;

import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.entity.ProductDetailImage;
import Fream_back.improve_Fream_Back.product.repository.ProductDetailImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductDetailImageCommandService {
    private final ProductDetailImageRepository productDetailImageRepository;

    public ProductDetailImage createProductDetailImage(String imageUrl, ProductColor productColor) {
        ProductDetailImage detailImage = ProductDetailImage.builder()
                .imageUrl(imageUrl)
                .productColor(productColor)
                .build();
        return productDetailImageRepository.save(detailImage);
    }

    public void deleteProductDetailImage(Long imageId) {
        ProductDetailImage detailImage = productDetailImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상세 이미지를 찾을 수 없습니다."));
        productDetailImageRepository.delete(detailImage);
    }
}
