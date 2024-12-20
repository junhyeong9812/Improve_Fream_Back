package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.ProductColorCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductColorUpdateRequestDto;
import Fream_back.improve_Fream_Back.product.service.productColor.ProductColorCommandService;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product-colors")
@RequiredArgsConstructor
public class ProductColorCommandController {

    private final ProductColorCommandService productColorCommandService;
    private final UserQueryService userQueryService; // 권한 확인 서비스

    // SecurityContext에서 이메일 추출
    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal(); // 이메일 반환
        }
        throw new IllegalStateException("인증된 사용자가 없습니다.");
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> createProductColor(
            @PathVariable Long productId,
            @RequestBody ProductColorCreateRequestDto requestDto,
            @RequestParam MultipartFile thumbnailImage,
            @RequestParam List<MultipartFile> images,
            @RequestParam List<MultipartFile> detailImages) {

        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        productColorCommandService.createProductColor(requestDto, thumbnailImage, images, detailImages, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productColorId}")
    public ResponseEntity<Void> updateProductColor(
            @PathVariable Long productColorId,
            @RequestBody ProductColorUpdateRequestDto requestDto,
            @RequestParam(required = false) MultipartFile thumbnailImage,
            @RequestParam(required = false) List<MultipartFile> newImages,
            @RequestParam(required = false) List<MultipartFile> newDetailImages) {

        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        productColorCommandService.updateProductColor(productColorId, requestDto, thumbnailImage, newImages, newDetailImages);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productColorId}")
    public ResponseEntity<Void> deleteProductColor(@PathVariable Long productColorId) {
        String email = extractEmailFromSecurityContext();
        userQueryService.checkAdminRole(email); // 관리자 권한 확인

        productColorCommandService.deleteProductColor(productColorId);
        return ResponseEntity.noContent().build();
    }
}
