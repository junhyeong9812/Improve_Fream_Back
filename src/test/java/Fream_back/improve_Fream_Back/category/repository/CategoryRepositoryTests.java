package Fream_back.improve_Fream_Back.category.repository;

import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.Category.repository.MainCategoryRepository;
import Fream_back.improve_Fream_Back.Category.repository.SubCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTests {

    @Autowired
    private MainCategoryRepository mainCategoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Test
    @DisplayName("메인 카테고리 저장 및 조회 테스트")
    public void testSaveAndFindMainCategory() {
        // given
        MainCategory mainCategory = MainCategory.builder().name("의류").build();
        mainCategoryRepository.save(mainCategory);

        // when
        Optional<MainCategory> retrievedCategory = mainCategoryRepository.findById(mainCategory.getId());

        // then
        assertThat(retrievedCategory).isPresent();
        assertThat(retrievedCategory.get().getName()).isEqualTo("의류");
    }

    @Test
    @DisplayName("서브 카테고리 저장 및 메인 카테고리와 함께 조회 테스트")
    public void testSaveAndFindSubCategoryWithMainCategory() {
        // given
        MainCategory mainCategory = MainCategory.builder().name("신발").build();
        mainCategoryRepository.save(mainCategory);

        SubCategory subCategory = SubCategory.builder().name("운동화").mainCategory(mainCategory).build();
        subCategoryRepository.save(subCategory);

        // when
        SubCategory retrievedSubCategory = subCategoryRepository.findByIdWithMainCategory(subCategory.getId());

        // then
        assertThat(retrievedSubCategory).isNotNull();
        assertThat(retrievedSubCategory.getName()).isEqualTo("운동화");
        assertThat(retrievedSubCategory.getMainCategory()).isNotNull();
        assertThat(retrievedSubCategory.getMainCategory().getName()).isEqualTo("신발");
    }

    @Test
    @DisplayName("모든 서브 카테고리와 메인 카테고리 함께 조회 테스트")
    public void testFindAllSubCategoriesWithMainCategory() {
        // given
        MainCategory mainCategory1 = MainCategory.builder().name("의류").build();
        MainCategory mainCategory2 = MainCategory.builder().name("가방").build();
        mainCategoryRepository.save(mainCategory1);
        mainCategoryRepository.save(mainCategory2);

        SubCategory subCategory1 = SubCategory.builder().name("티셔츠").mainCategory(mainCategory1).build();
        SubCategory subCategory2 = SubCategory.builder().name("백팩").mainCategory(mainCategory2).build();
        subCategoryRepository.save(subCategory1);
        subCategoryRepository.save(subCategory2);

        // when
        List<SubCategory> subCategories = subCategoryRepository.findAllWithMainCategory();

        // then
        assertThat(subCategories).hasSize(2);
        assertThat(subCategories.get(0).getMainCategory()).isNotNull();
        assertThat(subCategories.get(1).getMainCategory()).isNotNull();
    }

    @Test
    @DisplayName("메인 카테고리 삭제 테스트")
    public void testDeleteMainCategory() {
        // given
        MainCategory mainCategory = MainCategory.builder().name("의류").build();
        mainCategoryRepository.save(mainCategory);

        // when
        mainCategoryRepository.delete(mainCategory);
        Optional<MainCategory> retrievedCategory = mainCategoryRepository.findById(mainCategory.getId());

        // then
        assertThat(retrievedCategory).isNotPresent();
    }
    @Test
    @DisplayName("서브 카테고리 삭제 테스트")
    public void testDeleteSubCategory() {
        // given
        MainCategory mainCategory = MainCategory.builder().name("의류").build();
        mainCategoryRepository.save(mainCategory);

        SubCategory subCategory = SubCategory.builder().name("티셔츠").mainCategory(mainCategory).build();
        subCategoryRepository.save(subCategory);

        // when
        subCategoryRepository.delete(subCategory);
        Optional<SubCategory> retrievedSubCategory = subCategoryRepository.findById(subCategory.getId());

        // then
        assertThat(retrievedSubCategory).isNotPresent();
    }
}