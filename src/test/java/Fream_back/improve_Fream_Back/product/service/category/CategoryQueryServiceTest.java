package Fream_back.improve_Fream_Back.product.service.category;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.CategoryResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.repository.CategoryRepository;
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
@Import(TestProductConfig.class)
@Transactional
class CategoryQueryServiceTest {

    @Autowired
    private CategoryQueryService categoryQueryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("메인 카테고리 목록 조회")
    void findAllMainCategories() {
        // When
        List<CategoryResponseDto> mainCategories = categoryQueryService.findAllMainCategories();

        // Then
        assertThat(mainCategories).isNotEmpty();
        assertThat(mainCategories).extracting(CategoryResponseDto::getName)
                .containsExactlyInAnyOrder("Shoes");
    }

    @Test
    @DisplayName("특정 메인 카테고리의 서브 카테고리 목록 조회")
    void findSubCategoriesByMainCategory() {
        // Given
        String mainCategoryName = "Shoes";

        // When
        List<CategoryResponseDto> subCategories = categoryQueryService.findSubCategoriesByMainCategory(mainCategoryName);

        // Then
        assertThat(subCategories).isNotEmpty();
        assertThat(subCategories).extracting(CategoryResponseDto::getName)
                .containsExactlyInAnyOrder("Sneakers");
    }

    @Test
    @DisplayName("최상위 카테고리 조회")
    void findRootCategory() {
        // Given
        Category subCategory = categoryRepository.findByNameAndParentCategoryIsNotNull("Sneakers")
                .orElseThrow(() -> new IllegalArgumentException("서브 카테고리가 존재하지 않습니다."));

        // When
        Category rootCategory = categoryQueryService.findRootCategory(subCategory);

        // Then
        assertThat(rootCategory).isNotNull();
        assertThat(rootCategory.getName()).isEqualTo("Shoes");
    }

    @Test
    @DisplayName("ID로 카테고리 조회 및 최상위 카테고리 반환")
    void findRootCategoryById() {
        // Given
        Category subCategory = categoryRepository.findByNameAndParentCategoryIsNotNull("Sneakers")
                .orElseThrow(() -> new IllegalArgumentException("서브 카테고리가 존재하지 않습니다."));
        Long subCategoryId = subCategory.getId();

        // When
        Category rootCategory = categoryQueryService.findRootCategoryById(subCategoryId);

        // Then
        assertThat(rootCategory).isNotNull();
        assertThat(rootCategory.getName()).isEqualTo("Shoes");
    }

    @Test
    @DisplayName("존재하지 않는 메인 카테고리 조회 시 예외 발생")
    void findSubCategoriesByNonExistentMainCategory() {
        // Given
        String mainCategoryName = "NonExistentCategory";

        // When & Then
        assertThatThrownBy(() -> categoryQueryService.findSubCategoriesByMainCategory(mainCategoryName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메인 카테고리가 존재하지 않습니다.");
    }
}
