package Fream_back.improve_Fream_Back.product.service.brand;

import Fream_back.improve_Fream_Back.product.dto.BrandRequestDto;
import Fream_back.improve_Fream_Back.product.dto.BrandResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.repository.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BrandCommandService {

    private final BrandRepository brandRepository;

    public BrandResponseDto createBrand(BrandRequestDto request) {
        Brand brand = Brand.builder()
                .name(request.getName())
                .build();
        brandRepository.save(brand);
        return BrandResponseDto.fromEntity(brand);
    }

    public BrandResponseDto updateBrand(Long id, BrandRequestDto request) {
        // 기존 브랜드 조회
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        // 필요한 필드만 업데이트 (더티체크 적용)
        brand.updateName(request.getName());
        return BrandResponseDto.fromEntity(brand);
    }

    public void deleteBrand(String name) {
        Brand brand = brandRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        brandRepository.delete(brand);
    }
}
