package Fream_back.improve_Fream_Back.product.service.category;

import Fream_back.improve_Fream_Back.product.config.TestProductConfig;
import Fream_back.improve_Fream_Back.product.dto.CategoryRequestDto;
import Fream_back.improve_Fream_Back.product.dto.CategoryResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.repository.CategoryRepository;
import Fream_back.improve_Fream_Back.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestProductConfig.class)
@Transactional
class CategoryCommandServiceTest {

    @Autowired
    private CategoryCommandService categoryCommandService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestProductConfig.TestData testData;

    @Test
    @DisplayName("메인 카테고리 생성 테스트")
    void createMainCategory() {
        // Given
        CategoryRequestDto request = new CategoryRequestDto("Accessories", null);

        // When
        CategoryResponseDto response = categoryCommandService.createCategory(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Accessories");

        Category savedCategory = categoryRepository.findByNameAndParentCategoryIsNull("Accessories").orElseThrow();
        assertThat(savedCategory.getName()).isEqualTo("Accessories");
        assertThat(savedCategory.getParentCategory()).isNull();
    }

    @Test
    @DisplayName("서브 카테고리 생성 테스트")
    void createSubCategory() {
        // Given
        CategoryRequestDto request = new CategoryRequestDto("Shoes", "Boots");

        // When
        CategoryResponseDto response = categoryCommandService.createCategory(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Boots");

        Category subCategory = categoryRepository.findByNameAndParentCategory("Boots",
                categoryRepository.findByNameAndParentCategoryIsNull("Shoes").orElseThrow()).orElseThrow();
        assertThat(subCategory.getName()).isEqualTo("Boots");
        assertThat(subCategory.getParentCategory().getName()).isEqualTo("Shoes");
    }

    @Test
    @DisplayName("중복된 메인 카테고리 생성 시 예외 발생")
    void createDuplicateMainCategory() {
        // Given
        CategoryRequestDto request = new CategoryRequestDto("Shoes", null);

        // When & Then
        assertThatThrownBy(() -> categoryCommandService.createCategory(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 메인 카테고리 이름입니다.");
    }

    @Test
    @DisplayName("중복된 서브 카테고리 생성 시 예외 발생")
    void createDuplicateSubCategory() {
        // Given
        CategoryRequestDto request = new CategoryRequestDto("Shoes", "Sneakers");

        // When & Then
        assertThatThrownBy(() -> categoryCommandService.createCategory(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 상위 카테고리 아래에 동일한 이름의 서브 카테고리가 존재합니다.");
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateCategory() {
        // Given
        Category mainCategory = testData.getCategories().get(0); // Shoes
        CategoryRequestDto request = new CategoryRequestDto("Updated Shoes", null);

        // When
        CategoryResponseDto response = categoryCommandService.updateCategory(mainCategory.getId(), request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Shoes");

        Category updatedCategory = categoryRepository.findById(mainCategory.getId()).orElseThrow();
        assertThat(updatedCategory.getName()).isEqualTo("Updated Shoes");
    }

    @Test
    @DisplayName("상품이 포함된 카테고리 삭제 시 예외 발생")
    void deleteCategoryWithProducts() {
        // Given
        Category categoryWithProducts = testData.getCategories().get(1); // Sneakers

        // When & Then
        assertThatThrownBy(() -> categoryCommandService.deleteCategory(categoryWithProducts.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("포함된 상품을 먼저 삭제해야 합니다.");
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategory() {
        // Given
        Category category = categoryRepository.save(Category.builder().name("Accessories").build());

        // When
        categoryCommandService.deleteCategory(category.getId());

        // Then
        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }
}
