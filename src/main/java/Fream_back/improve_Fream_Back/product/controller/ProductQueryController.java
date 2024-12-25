package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import Fream_back.improve_Fream_Back.product.repository.SortOption;
import Fream_back.improve_Fream_Back.product.service.product.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;

    @GetMapping
    public ResponseEntity<Page<ProductSearchResponseDto>> searchProducts(
            @ModelAttribute ProductSearchDto searchRequest,
            Pageable pageable) {
        // 유효성 검증
        searchRequest.validate();

        Page<ProductSearchResponseDto> response = productQueryService.searchProducts(
                searchRequest.getKeyword(),
                searchRequest.getCategoryIds(),
                searchRequest.getGenders(),
                searchRequest.getBrandIds(),
                searchRequest.getCollectionIds(),
                searchRequest.getColors(),
                searchRequest.getSizes(),
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                searchRequest.getSortOption(),
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/detail")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            @PathVariable("productId") Long productId,
            @RequestParam("colorName") String colorName) {
        ProductDetailResponseDto response = productQueryService.getProductDetail(productId, colorName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<byte[]> getProductImage(
            @PathVariable("productId") Long productId,
            @RequestParam("imageName") String imageName) throws Exception {
        // 파일 경로 설정
        String directory = "product/" + productId;
        File imageFile = new File(directory + File.separator + imageName);

        // 파일이 존재하지 않을 경우 예외 처리
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다.");
        }

        // 파일 내용 읽기
        byte[] imageBytes = Files.readAllBytes(Paths.get(imageFile.getPath()));

        // 응답 생성 (Content-Type 설정)
        return ResponseEntity.ok()
                .header("Content-Type", Files.probeContentType(imageFile.toPath()))
                .body(imageBytes);
    }
}
