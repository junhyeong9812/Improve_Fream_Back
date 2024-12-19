package Fream_back.improve_Fream_Back.product.service.productColor;

import Fream_back.improve_Fream_Back.product.entity.ProductColor;
import Fream_back.improve_Fream_Back.product.repository.ProductColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductColorQueryService {

    private final ProductColorRepository productColorRepository;

    // ProductColor ID로 조회
    public ProductColor findById(Long productColorId) {
        return productColorRepository.findById(productColorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ProductColor를 찾을 수 없습니다: " + productColorId));
    }
}