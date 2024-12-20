package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.service.product.ProductCommandService;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final ProductCommandService productCommandService;
    private final UserQueryService userQueryService; // 권한 확인 서비스

    // SecurityContext에서 이메일 추출
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
    }

    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> createProduct(@RequestBody ProductCreateRequestDto request) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        ProductCreateResponseDto response = productCommandService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductUpdateResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto request) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        ProductUpdateResponseDto response = productCommandService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        productCommandService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
