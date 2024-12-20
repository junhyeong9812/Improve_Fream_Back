package Fream_back.improve_Fream_Back.product.repository;

import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.user.config.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestQueryDslConfig.class) // QueryDSL 설정이 필요하다면 추가
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("메인 카테고리 저장 및 조회")
    void saveAndFindMainCategory() {
        // Given
        Category mainCategory = Category.builder()
                .name("Clothing")
                .build();
        categoryRepository.save(mainCategory);

        // When
        Optional<Category> result = categoryRepository.findByNameAndParentCategoryIsNull("Clothing");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Clothing");
        assertThat(result.get().getParentCategory()).isNull();
    }

    @Test
    @DisplayName("서브 카테고리 저장 및 조회")
    void saveAndFindSubCategory() {
        // Given
        Category mainCategory = Category.builder()
                .name("Clothing")
                .build();
        Category subCategory = Category.builder()
                .name("T-Shirts")
                .build();

        mainCategory.addSubCategory(subCategory);
        categoryRepository.save(mainCategory);

        // When
        Optional<Category> result = categoryRepository.findByNameAndParentCategory("T-Shirts", mainCategory);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("T-Shirts");
        assertThat(result.get().getParentCategory()).isEqualTo(mainCategory);
    }

    @Test
    @DisplayName("모든 메인 카테고리 조회")
    void findAllMainCategories() {
        // Given
        categoryRepository.save(Category.builder().name("Clothing").build());
        categoryRepository.save(Category.builder().name("Shoes").build());

        // When
        List<Category> result = categoryRepository.findByParentCategoryIsNullOrderByNameDesc();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Shoes"); // 이름 내림차순 확인
        assertThat(result.get(1).getName()).isEqualTo("Clothing");
    }

    @Test
    @DisplayName("특정 메인 카테고리의 서브 카테고리 조회")
    void findAllSubCategoriesByMainCategory() {
        // Given
        Category mainCategory = Category.builder()
                .name("Clothing")
                .build();
        Category subCategory1 = Category.builder()
                .name("T-Shirts")
                .build();
        Category subCategory2 = Category.builder()
                .name("Jeans")
                .build();

        mainCategory.addSubCategory(subCategory1);
        mainCategory.addSubCategory(subCategory2);
        categoryRepository.save(mainCategory);

        // When
        List<Category> result = categoryRepository.findByParentCategoryOrderByNameDesc(mainCategory);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("T-Shirts");
        assertThat(result.get(1).getName()).isEqualTo("Jeans");
    }

    @Test
    @DisplayName("ID로 카테고리와 상위 카테고리 조회")
    void findWithParentById() {
        // Given
        Category mainCategory = Category.builder()
                .name("Clothing")
                .build();
        Category subCategory = Category.builder()
                .name("T-Shirts")
                .build();

        mainCategory.addSubCategory(subCategory);
        categoryRepository.save(mainCategory);

        // When
        Optional<Category> result = categoryRepository.findWithParentById(subCategory.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("T-Shirts");
        assertThat(result.get().getParentCategory()).isEqualTo(mainCategory);
    }
}
