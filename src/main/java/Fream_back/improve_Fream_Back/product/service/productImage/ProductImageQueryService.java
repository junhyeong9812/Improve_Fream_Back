package Fream_back.improve_Fream_Back.product.service.productImage;

import Fream_back.improve_Fream_Back.product.entity.ProductImage;
import Fream_back.improve_Fream_Back.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageQueryService {
    private final ProductImageRepository productImageRepository;

    public List<ProductImage> findAllByProductColorId(Long productColorId) {
        return productImageRepository.findAllByProductColorId(productColorId);
    }
}