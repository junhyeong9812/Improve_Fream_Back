package Fream_back.improve_Fream_Back.product.service.category;

import Fream_back.improve_Fream_Back.product.dto.CategoryRequestDto;
import Fream_back.improve_Fream_Back.product.dto.CategoryResponseDto;
import Fream_back.improve_Fream_Back.product.entity.Category;
import Fream_back.improve_Fream_Back.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryCommandService {

    private final CategoryRepository categoryRepository;

    public CategoryCommandService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        Category mainCategory = categoryRepository.findByNameAndParentCategoryIsNull(request.getMainCategoryName())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(request.getMainCategoryName()).build()));

        if (request.getSubCategoryName() != null) {
            Category subCategory = Category.builder()
                    .name(request.getSubCategoryName())
                    .parentCategory(mainCategory)
                    .build();
            categoryRepository.save(subCategory);
            return CategoryResponseDto.fromEntity(subCategory);
        }
        return CategoryResponseDto.fromEntity(mainCategory);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        if (request.getMainCategoryName() != null) {
            category.updateName(request.getMainCategoryName());
        }
        if (request.getSubCategoryName() != null && category.getParentCategory() != null) {
            category.getParentCategory().updateName(request.getSubCategoryName());
        }
        return CategoryResponseDto.fromEntity(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        categoryRepository.delete(category);
    }
}
