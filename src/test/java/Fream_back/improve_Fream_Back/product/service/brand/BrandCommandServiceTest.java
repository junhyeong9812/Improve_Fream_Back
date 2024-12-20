package Fream_back.improve_Fream_Back.product.service.brand;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.BrandRequestDto;
import Fream_back.improve_Fream_Back.product.dto.BrandResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.product.repository.BrandRepository;
import Fream_back.improve_Fream_Back.user.config.TestConfig;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import({TestConfig.class,TestProductConfig.class})
@Transactional
class BrandCommandServiceTest {

    @Autowired
    private BrandCommandService brandCommandService;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드 생성 테스트")
    void createBrand() {
        // Given
        BrandRequestDto request = new BrandRequestDto("Puma");

        // When
        BrandResponseDto response = brandCommandService.createBrand(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Puma");

        assertThat(brandRepository.findByName("Puma")).isPresent();
    }

    @Test
    @DisplayName("브랜드 업데이트 테스트")
    void updateBrand() {
        // Given
        BrandRequestDto request = new BrandRequestDto("Updated Nike");
        Long brandId = brandRepository.findByName("Nike").orElseThrow().getId();

        // When
        BrandResponseDto response = brandCommandService.updateBrand(brandId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Nike");
        assertThat(brandRepository.findById(brandId).orElseThrow().getName()).isEqualTo("Updated Nike");
    }

    @Test
    @DisplayName("연관된 상품이 없을 때 브랜드 삭제")
    void deleteBrandWithoutAssociatedProducts() {
        // Given
        Brand newBrand = brandRepository.save(Brand.builder().name("TemporaryBrand").build());
        assertThat(brandRepository.findByName("TemporaryBrand")).isPresent();

        // When
        brandCommandService.deleteBrand("TemporaryBrand");

        // Then
        assertThat(brandRepository.findByName("TemporaryBrand")).isEmpty();
    }


    @Test
    @DisplayName("존재하지 않는 브랜드 삭제 시 예외 처리 테스트")
    void deleteNonExistentBrand() {
        // Given
        String nonExistentBrandName = "NonExistentBrand";

        // When & Then
        assertThatThrownBy(() -> brandCommandService.deleteBrand(nonExistentBrandName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 브랜드입니다.");
    }
}
