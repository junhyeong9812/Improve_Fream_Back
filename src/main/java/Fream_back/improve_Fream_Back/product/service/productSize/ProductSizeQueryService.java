package Fream_back.improve_Fream_Back.product.service.productSize;

import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.repository.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductSizeQueryService {

    private final ProductSizeRepository productSizeRepository;

    public Optional<ProductSize> findByColorIdAndSize(Long colorId, String size) {
        return productSizeRepository.findByProductColorIdAndSize(colorId, size);
    }
    public List<String> findSizesByColorId(Long productColorId) {
        return productSizeRepository.findAllByProductColorId(productColorId)
                .stream()
                .map(ProductSize::getSize) // 사이즈 값만 추출
                .collect(Collectors.toList());
    }
}