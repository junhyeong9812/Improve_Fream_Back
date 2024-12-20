package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Brand;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(TestQueryDslConfig.class) // QueryDSL 테스트 설정 임포트
class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드 저장 및 조회 테스트")
    void saveAndFindBrand() {
        // Given
        Brand brand = Brand.builder()
                .name("Nike")
                .build();

        // When
        Brand savedBrand = brandRepository.save(brand);
        Optional<Brand> foundBrand = brandRepository.findById(savedBrand.getId());

        // Then
        assertThat(foundBrand).isPresent();
        assertThat(foundBrand.get().getName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("브랜드 이름으로 조회 테스트")
    void findByName() {
        // Given
        Brand brand = Brand.builder()
                .name("Adidas")
                .build();
        brandRepository.save(brand);

        // When
        Optional<Brand> foundBrand = brandRepository.findByName("Adidas");

        // Then
        assertThat(foundBrand).isPresent();
        assertThat(foundBrand.get().getName()).isEqualTo("Adidas");
    }

    @Test
    @DisplayName("브랜드 이름 내림차순 조회 테스트")
    void findAllByOrderByNameDesc() {
        // Given
        brandRepository.save(Brand.builder().name("Zara").build());
        brandRepository.save(Brand.builder().name("Apple").build());
        brandRepository.save(Brand.builder().name("Nike").build());

        // When
        List<Brand> brands = brandRepository.findAllByOrderByNameDesc();

        // Then
        assertThat(brands).hasSize(3);
        assertThat(brands.get(0).getName()).isEqualTo("Zara");
        assertThat(brands.get(1).getName()).isEqualTo("Nike");
        assertThat(brands.get(2).getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("브랜드 저장 실패 테스트 - 이름이 null인 경우")
    void saveBrandWithNullName() {
        // Given
        Brand brand = Brand.builder()
                .name(null)
                .build();

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            brandRepository.save(brand);
        });
    }

    @Test
    @DisplayName("브랜드 이름 업데이트 테스트")
    void updateBrandName() {
        // Given
        Brand brand = Brand.builder()
                .name("Puma")
                .build();
        Brand savedBrand = brandRepository.save(brand);

        // When
        savedBrand.updateName("Updated Puma");
        brandRepository.save(savedBrand);

        // Then
        Optional<Brand> updatedBrand = brandRepository.findById(savedBrand.getId());
        assertThat(updatedBrand).isPresent();
        assertThat(updatedBrand.get().getName()).isEqualTo("Updated Puma");
    }
}
