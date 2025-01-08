package Fream_back.improve_Fream_Back.product.service.brand;

import Fream_back.improve_Fream_Back.product.dto.BrandResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BrandQueryService {

    private final BrandRepository brandRepository;

    public BrandQueryService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public BrandResponseDto findByName(String name) {
        Brand brand = brandRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
        return BrandResponseDto.fromEntity(brand); // 엔티티 -> DTO 변환
    }

    public List<BrandResponseDto> findAllBrands() {
        return brandRepository.findAllByOrderByNameDesc()
                .stream()
                .map(BrandResponseDto::fromEntity) // 엔티티 -> DTO 변환
                .collect(Collectors.toList());
    }
    public Brand findById(Long brandId){
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
    }

}
