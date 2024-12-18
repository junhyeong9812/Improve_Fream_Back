package Fream_back.improve_Fream_Back.product.service.product;

import Fream_back.improve_Fream_Back.product.entity.Product;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductEntityService {

    private final ProductRepository productRepository;

    // ID를 기반으로 Product 엔티티 조회
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 Product가 존재하지 않습니다. ID: " + id));
    }
}