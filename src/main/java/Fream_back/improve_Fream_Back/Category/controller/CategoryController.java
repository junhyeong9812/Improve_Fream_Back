//package Fream_back.improve_Fream_Back.Category.controller;
//
//import Fream_back.improve_Fream_Back.Category.dto.CategoryRequestDto;
//import Fream_back.improve_Fream_Back.Category.dto.CategoryResponseDto;
//import Fream_back.improve_Fream_Back.Category.dto.SubCategoryRequestDto;
//import Fream_back.improve_Fream_Back.Category.service.CategoryService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Set;
//
//@RestController
//@RequestMapping("/api/categories")
//@RequiredArgsConstructor
//public class CategoryController {
//
//    private final CategoryService categoryService;
//
//    /**
//     * 상위 + 하위 카테고리 동시 생성 엔드포인트
//     * 로그인된 사용자만 접근 가능하며, 주어진 상위 카테고리와 하위 카테고리 정보를 저장합니다.
//     *
//     * @param requestDto 상위 카테고리 이름과 하위 카테고리 이름 리스트가 포함된 DTO
//     * @return ResponseEntity<CategoryResponseDto> 생성된 상위 및 하위 카테고리 정보 반환
//     */
//    @PostMapping
//    public ResponseEntity<CategoryResponseDto> createCategoryWithSubCategories(@RequestBody CategoryRequestDto requestDto) {
//        String loginId = getAuthenticatedUser();
//        if (loginId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//
//        return ResponseEntity.ok(categoryService.createCategoryWithSubCategories(requestDto));
//    }
//
//    /**
//     * 하위 카테고리 별도 추가 엔드포인트
//     * 로그인된 사용자만 접근 가능하며, 특정 상위 카테고리에 새 하위 카테고리를 추가합니다.
//     *
//     * @param requestDto 상위 카테고리 ID와 새로 추가할 하위 카테고리 이름이 포함된 DTO
//     * @return ResponseEntity<Void> 성공적으로 추가 시 204 상태 반환
//     */
//    @PostMapping("/sub-category")
//    public ResponseEntity<Void> createSubCategory(@RequestBody SubCategoryRequestDto requestDto) {
//        String loginId = getAuthenticatedUser();
//        if (loginId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        categoryService.createSubCategory(requestDto);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * 모든 카테고리 조회 엔드포인트
//     * 저장된 모든 상위 카테고리와 하위 카테고리 정보를 반환합니다.
//     *
//     * @return ResponseEntity<Set<CategoryResponseDto>> 상위 및 하위 카테고리 정보 반환
//     */
//    @GetMapping
//    public ResponseEntity<Set<CategoryResponseDto>> getAllCategories() {
//        return ResponseEntity.ok(categoryService.getAllCategories());
//    }
//
//    /**
//     * 인증된 사용자 ID를 반환하는 메서드
//     * SecurityContextHolder에서 인증된 사용자의 로그인 ID를 가져옵니다.
//     *
//     * @return String 로그인 ID (없을 경우 null)
//     */
//    private String getAuthenticatedUser() {
//        try {
//            return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//    /**
//     * 상위 카테고리 이름 수정 엔드포인트
//     *
//     * @param mainCategoryId 수정할 상위 카테고리의 ID
//     * @param newName 새로운 상위 카테고리 이름
//     * @return ResponseEntity 204 No Content 응답
//     */
//    @PutMapping("/{mainCategoryId}")
//    public ResponseEntity<Void> updateMainCategory(@PathVariable Long mainCategoryId,
//                                                   @RequestParam String newName) {
//        categoryService.updateMainCategory(mainCategoryId, newName);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * 하위 카테고리 이름 수정 엔드포인트
//     *
//     * @param subCategoryId 수정할 하위 카테고리의 ID
//     * @param newName 새로운 하위 카테고리 이름
//     * @return ResponseEntity 204 No Content 응답
//     */
//    @PutMapping("/sub-category/{subCategoryId}")
//    public ResponseEntity<Void> updateSubCategory(@PathVariable Long subCategoryId,
//                                                  @RequestParam String newName) {
//        categoryService.updateSubCategory(subCategoryId, newName);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * 하위 카테고리 삭제 엔드포인트
//     *
//     * @param subCategoryId 삭제할 하위 카테고리의 ID
//     * @return ResponseEntity 204 No Content 응답
//     */
//    @DeleteMapping("/sub-category/{subCategoryId}")
//    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long subCategoryId) {
//        categoryService.deleteSubCategory(subCategoryId);
//        return ResponseEntity.noContent().build();
//    }
//
//    /**
//     * 상위 카테고리 삭제 엔드포인트
//     *
//     * @param mainCategoryId 삭제할 상위 카테고리의 ID
//     * @return ResponseEntity 204 No Content 응답
//     */
//    @DeleteMapping("/{mainCategoryId}")
//    public ResponseEntity<Void> deleteMainCategory(@PathVariable Long mainCategoryId) {
//        categoryService.deleteMainCategory(mainCategoryId);
//        return ResponseEntity.noContent().build();
//    }
//    /**
//     * 하위 카테고리의 상위 카테고리를 변경하는 엔드포인트
//     * 주어진 하위 카테고리 ID와 새로운 상위 카테고리 ID를 기반으로 관계를 변경합니다.
//     * 로그인된 사용자만 접근 가능합니다.
//     *
//     * @param subCategoryId 변경할 하위 카테고리의 ID
//     * @param newMainCategoryId 새로운 상위 카테고리의 ID
//     * @return ResponseEntity<Void> 성공적으로 변경 시 204 상태 반환
//     */
//    @PatchMapping("/sub-category/{subCategoryId}/change-main-category")
//    public ResponseEntity<Void> changeSubCategoryMainCategory(@PathVariable Long subCategoryId,
//                                                              @RequestParam Long newMainCategoryId) {
//        String loginId = getAuthenticatedUser();
//        if (loginId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        categoryService.changeSubCategoryMainCategory(subCategoryId, newMainCategoryId);
//        return ResponseEntity.noContent().build();
//    }
//}