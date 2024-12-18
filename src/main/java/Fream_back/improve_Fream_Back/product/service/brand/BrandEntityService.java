package Fream_back.improve_Fream_Back.product.service.brand;

import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BrandEntityService {

    private final BrandRepository brandRepository;

    public Brand findByName(String name) {
        return brandRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("브랜드가 존재하지 않습니다."));
    }
}
