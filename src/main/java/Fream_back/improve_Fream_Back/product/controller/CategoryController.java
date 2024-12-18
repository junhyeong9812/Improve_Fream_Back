package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.CategoryRequestDto;
import Fream_back.improve_Fream_Back.product.dto.CategoryResponseDto;
import Fream_back.improve_Fream_Back.product.service.category.CategoryCommandService;
import Fream_back.improve_Fream_Back.product.service.category.CategoryQueryService;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;
    private final UserQueryService userQueryService;

    // 관리자 권한 확인용 메서드
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto request) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 권한 확인

        return ResponseEntity.ok(categoryCommandService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDto request) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 권한 확인

        return ResponseEntity.ok(categoryCommandService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 권한 확인

        categoryCommandService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/main")
    public ResponseEntity<List<CategoryResponseDto>> getAllMainCategories() {
        return ResponseEntity.ok(categoryQueryService.findAllMainCategories());
    }

    @GetMapping("/sub/{mainCategoryName}")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategoriesByMain(@PathVariable String mainCategoryName) {
        return ResponseEntity.ok(categoryQueryService.findSubCategoriesByMainCategory(mainCategoryName));
    }
}
