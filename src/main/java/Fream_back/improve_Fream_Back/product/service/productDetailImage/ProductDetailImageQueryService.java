package Fream_back.improve_Fream_Back.product.service.productDetailImage;

import Fream_back.improve_Fream_Back.product.entity.ProductDetailImage;
import Fream_back.improve_Fream_Back.product.repository.ProductDetailImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductDetailImageQueryService {
    private final ProductDetailImageRepository productDetailImageRepository;

    public List<ProductDetailImage> findAllByProductColorId(Long productColorId) {
        return productDetailImageRepository.findAllByProductColorId(productColorId);
    }
    public boolean existsByProductColorId(Long productColorId) {
        return productDetailImageRepository.existsByProductColorId(productColorId);
    }
}