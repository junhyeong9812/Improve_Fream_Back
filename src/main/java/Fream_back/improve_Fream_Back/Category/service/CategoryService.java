package Fream_back.improve_Fream_Back.Category.service;

import Fream_back.improve_Fream_Back.Category.dto.CategoryRequestDto;
import Fream_back.improve_Fream_Back.Category.dto.CategoryResponseDto;
import Fream_back.improve_Fream_Back.Category.dto.SubCategoryRequestDto;
import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import Fream_back.improve_Fream_Back.Category.repository.MainCategoryRepository;
import Fream_back.improve_Fream_Back.Category.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    /**
     * 상위 카테고리와 하위 카테고리를 동시에 생성하는 서비스
     * 주어진 MainCategory 이름과 SubCategory 이름 리스트를 기반으로 카테고리 생성.
     *
     * @param requestDto 상위 카테고리 이름 및 하위 카테고리 이름 리스트를 포함한 DTO
     * @return CategoryResponseDto 생성된 상위 카테고리 및 하위 카테고리 정보 반환
     */
    @Transactional
    public CategoryResponseDto createCategoryWithSubCategories(CategoryRequestDto requestDto) {
        // 상위 카테고리 생성
        MainCategory mainCategory = MainCategory.builder()
                .name(requestDto.getMainCategoryName())
                .build();

        // 하위 카테고리 추가
        Set<SubCategory> subCategories = requestDto.getSubCategoryNames().stream()
                .map(name -> SubCategory.builder().name(name).mainCategory(mainCategory).build())
                .collect(Collectors.toSet());

        mainCategory.getSubCategories().addAll(subCategories);

        // 저장
        MainCategory savedMainCategory = mainCategoryRepository.save(mainCategory);

        // 응답 DTO 생성
        Set<String> subCategoryNames = savedMainCategory.getSubCategories().stream()
                .map(SubCategory::getName)
                .collect(Collectors.toSet());

        return new CategoryResponseDto(savedMainCategory.getId(), savedMainCategory.getName(), subCategoryNames);
    }

    /**
     * 특정 상위 카테고리에 새로운 하위 카테고리를 추가하는 서비스
     * 주어진 MainCategory ID를 기반으로 상위 카테고리를 확인하고, 해당 카테고리에 하위 카테고리를 추가.
     *
     * @param requestDto 상위 카테고리 ID와 새로 추가할 하위 카테고리 이름을 포함한 DTO
     */
    @Transactional
    public void createSubCategory(SubCategoryRequestDto requestDto) {
        // 상위 카테고리 확인
        MainCategory mainCategory = mainCategoryRepository.findById(requestDto.getMainCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid MainCategory ID"));

        // 하위 카테고리 생성 및 저장
        SubCategory subCategory = SubCategory.builder()
                .name(requestDto.getSubCategoryName())
                .mainCategory(mainCategory)
                .build();
        subCategoryRepository.save(subCategory);
    }

    /**
     * 모든 상위 카테고리와 그에 속한 하위 카테고리를 조회하는 서비스
     * 저장된 모든 MainCategory와 그에 속한 SubCategory를 반환.
     *
     * @return Set<CategoryResponseDto> 모든 상위 카테고리와 하위 카테고리 정보 반환
     */
    @Transactional(readOnly = true)
    public Set<CategoryResponseDto> getAllCategories() {
        return mainCategoryRepository.findAll().stream()
                .map(mainCategory -> new CategoryResponseDto(
                        mainCategory.getId(),
                        mainCategory.getName(),
                        mainCategory.getSubCategories().stream()
                                .map(SubCategory::getName)
                                .collect(Collectors.toSet())))
                .collect(Collectors.toSet());
    }
    /**
     * 상위 카테고리 이름을 수정하는 서비스
     *
     * @param mainCategoryId 수정할 상위 카테고리의 ID
     * @param newName 새로운 상위 카테고리 이름
     */
    @Transactional
    public void updateMainCategory(Long mainCategoryId, String newName) {
        MainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("MainCategory ID does not exist."));
        mainCategory.updateName(newName); // 엔티티 내 수정 메서드 호출
    }

    /**
     * 하위 카테고리 이름을 수정하는 서비스
     *
     * @param subCategoryId 수정할 하위 카테고리의 ID
     * @param newName 새로운 하위 카테고리 이름
     */
    @Transactional
    public void updateSubCategory(Long subCategoryId, String newName) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("SubCategory ID does not exist."));
        subCategory.updateName(newName); // 엔티티 내 수정 메서드 호출
    }

    /**
     * 하위 카테고리를 삭제하는 서비스
     *
     * @param subCategoryId 삭제할 하위 카테고리의 ID
     */
    @Transactional
    public void deleteSubCategory(Long subCategoryId) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("SubCategory ID does not exist."));
        subCategoryRepository.delete(subCategory); // 삭제
    }

    /**
     * 상위 카테고리를 삭제하는 서비스
     *
     * @param mainCategoryId 삭제할 상위 카테고리의 ID
     */
    @Transactional
    public void deleteMainCategory(Long mainCategoryId) {
        MainCategory mainCategory = mainCategoryRepository.findById(mainCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("MainCategory ID does not exist."));
        mainCategoryRepository.delete(mainCategory); // 삭제
    }
    /**
     * 하위 카테고리의 상위 카테고리를 변경하는 서비스
     * 주어진 하위 카테고리 ID와 새로운 상위 카테고리 ID를 기반으로 관계를 변경합니다.
     *
     * @param subCategoryId 변경할 하위 카테고리의 ID
     * @param newMainCategoryId 새로운 상위 카테고리의 ID
     */
    @Transactional
    public void changeSubCategoryMainCategory(Long subCategoryId, Long newMainCategoryId) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("SubCategory ID does not exist."));

        MainCategory newMainCategory = mainCategoryRepository.findById(newMainCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("MainCategory ID does not exist."));

        subCategory.changeMainCategory(newMainCategory); // 엔티티 내 메서드를 호출하여 관계 변경
    }

}