package Fream_back.improve_Fream_Back.product.service.brand;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.BrandResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.repository.BrandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BrandQueryServiceTest {

    @Autowired
    private BrandQueryService brandQueryService;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드 이름으로 조회 테스트")
    void findByName() {
        // Given
        String brandName = "PUMA";
        Brand savedBrand = brandRepository.save(Brand.builder().name(brandName).build());

        // When
        BrandResponseDto result = brandQueryService.findByName(brandName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(brandName);
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 이름 조회 시 예외 발생 테스트")
    void findByNameNotFound() {
        // Given
        String nonExistentBrandName = "NonExistentBrand";

        // When & Then
        assertThatThrownBy(() -> brandQueryService.findByName(nonExistentBrandName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 브랜드가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("전체 브랜드 조회 테스트")
    void findAllBrands() {
        // Given
        Brand nike = brandRepository.save(Brand.builder().name("Nike").build());
        Brand adidas = brandRepository.save(Brand.builder().name("Adidas").build());
        Brand puma = brandRepository.save(Brand.builder().name("Puma").build());

        // When
        List<BrandResponseDto> result = brandQueryService.findAllBrands();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(BrandResponseDto::getName)
                .containsExactlyInAnyOrder("Nike", "Adidas", "Puma");
    }
}
