package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.*;
import Fream_back.improve_Fream_Back.product.dto.create.ProductCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.delete.ProductDeleteRequestDto;
import Fream_back.improve_Fream_Back.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 임시 URL 생성 엔드포인트
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
     *
     * @param productDto 생성할 상품의 상세 정보 DTO
     * @return 생성된 상품의 ID를 담은 DTO
     */
    @PostMapping
    public ResponseEntity<ProductIdResponseDto> createProduct(@RequestBody ProductCreateRequestDto productDto) {
        ProductIdResponseDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.ok(createdProduct);
    }
//    @PostMapping
//    public ResponseEntity<ProductIdResponseDto> createProduct(
//            @RequestBody ProductCreateRequestDto productDto,
//            @RequestParam("tempFilePaths") List<String> tempFilePaths) {
//        ProductIdResponseDto createdProduct = productService.createProduct(productDto, tempFilePaths);
//        return ResponseEntity.ok(createdProduct);
//    }

    /**
     * 상품 수정 엔드포인트
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
        ProductIdResponseDto updatedProduct = productService.updateProduct(productId, productDto, tempFilePaths);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * 상품 삭제 엔드포인트
     *
     * @param productId 삭제할 상품의 ID
     * @return 성공 메시지 (String)
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String responseMessage = productService.deleteProduct(new ProductDeleteRequestDto(productId));
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 단일 상품 조회 엔드포인트
     *
     * @param productId 조회할 상품 ID
     * @return 조회된 상품의 상세 정보 DTO
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId) {
        ProductResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /**
     * 필터링된 상품 조회 엔드포인트
     *
     * @param queryDslRequestDto 필터링 조건 DTO
     * @param pageable 페이징 정보
     * @return 필터링된 상품 리스트와 페이징 정보
     */
    @GetMapping("/filter")
    public ResponseEntity<Page<ProductQueryDslResponseDto>> getFilteredProducts(
            @ModelAttribute ProductQueryDslRequestDto queryDslRequestDto,
            Pageable pageable) {
        Page<ProductQueryDslResponseDto> filteredProducts = productService.searchFilteredProducts(queryDslRequestDto, pageable);
        return ResponseEntity.ok(filteredProducts);
    }
}
