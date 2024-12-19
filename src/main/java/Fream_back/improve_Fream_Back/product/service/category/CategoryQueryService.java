package Fream_back.improve_Fream_Back.product.service.category;

import Fream_back.improve_Fream_Back.product.dto.CategoryResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public CategoryQueryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 메인 카테고리 목록 조회
    public List<CategoryResponseDto> findAllMainCategories() {
        return categoryRepository.findByParentCategoryIsNullOrderByNameDesc()
                .stream()
                .map(CategoryResponseDto::fromEntity) // 엔티티 -> DTO 변환
                .collect(Collectors.toList());
    }

    // 특정 메인 카테고리에 대한 서브 카테고리 목록 조회
    public List<CategoryResponseDto> findSubCategoriesByMainCategory(String mainCategoryName) {
        Category mainCategory = categoryRepository.findByNameAndParentCategoryIsNull(mainCategoryName)
                .orElseThrow(() -> new IllegalArgumentException("메인 카테고리가 존재하지 않습니다."));
        return categoryRepository.findByParentCategoryOrderByNameDesc(mainCategory)
                .stream()
                .map(CategoryResponseDto::fromEntity) // 엔티티 -> DTO 변환
                .collect(Collectors.toList());
    }
    // 최상위 카테고리 조회 메서드
    public Category findRootCategory(Category category) {
        while (category.getParentCategory() != null) {
            category = category.getParentCategory();
        }
        return category;
    }

    // ID로 카테고리 조회 및 최상위 카테고리 반환
    public Category findRootCategoryById(Long categoryId) {
        Category category = categoryRepository.findWithParentById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
        return findRootCategory(category);
    }
}
