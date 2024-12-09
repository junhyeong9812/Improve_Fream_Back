package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.dto.create.ProductCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.delete.ProductDeleteRequestDto;
import Fream_back.improve_Fream_Back.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jwt/products")
@RequiredArgsConstructor
public class JwtProductController {

    private final ProductService productService;

    /**
     * 임시 URL 생성 엔드포인트
     * JWT 인증된 사용자만 호출 가능하며 이미지를 업로드하고 임시 URL을 생성합니다.
     *
     * @param file 업로드할 이미지 파일 (Multipart 형식)
     * @return 생성된 임시 URL (String)
     */
    @PostMapping("/temporary-url")
    public ResponseEntity<String> createTemporaryUrl(@RequestParam("file") MultipartFile file) {
        String tempUrl = productService.createTemporaryUrl(file);
        return ResponseEntity.ok(tempUrl);
    }

    /**
     * 상품 생성 엔드포인트
     * JWT 인증된 사용자만 호출 가능하며 상품 정보를 생성합니다.
     *
     * @param productDto 생성할 상품의 상세 정보 DTO
//     * @param tempFilePaths 임시 저장된 이미지 파일 경로 목록
     * @return 생성된 상품의 ID를 담은 DTO
     */
    @PostMapping
    public ResponseEntity<ProductIdResponseDto> createProduct(
            @RequestBody ProductCreateRequestDto productDto) {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Optional: 로그인 ID로 추가 작업 가능
        ProductIdResponseDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.ok(createdProduct);
    }

    /**
     * 상품 수정 엔드포인트
     * JWT 인증된 사용자만 호출 가능하며 상품 정보를 수정합니다.
     *
     * @param productId 수정할 상품 ID
     * @param productDto 수정할 상품의 상세 정보 DTO
     * @param tempFilePaths 임시 저장된 이미지 파일 경로 목록
     * @return 수정된 상품의 ID를 담은 DTO
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ProductIdResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto productDto,
            @RequestParam List<String> tempFilePaths) {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Optional: 로그인 ID로 추가 작업 가능
        ProductIdResponseDto updatedProduct = productService.updateProduct(productId, productDto, tempFilePaths);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * 상품 삭제 엔드포인트
     * JWT 인증된 사용자만 호출 가능하며 상품 정보를 삭제합니다.
     *
     * @param productId 삭제할 상품 ID
     * @return 성공 메시지 (String)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String loginId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Optional: 로그인 ID로 추가 작업 가능
        String responseMessage = productService.deleteProduct(new ProductDeleteRequestDto(productId));
        return ResponseEntity.ok(responseMessage);
    }
}

