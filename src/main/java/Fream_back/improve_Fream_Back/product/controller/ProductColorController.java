package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.dto.ProductColorCreateRequestDto;
import Fream_back.improve_Fream_Back.product.dto.ProductColorUpdateRequestDto;
import Fream_back.improve_Fream_Back.product.service.productColor.ProductColorCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product-colors")
@RequiredArgsConstructor
public class ProductColorController {

    private final ProductColorCommandService productColorCommandService;

    // 상품 색상 생성
    @PostMapping("/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProductColor(
            @PathVariable Long productId,
            @RequestPart ProductColorCreateRequestDto requestDto,
            @RequestPart(required = false) MultipartFile thumbnailImage,
            @RequestPart(required = false) List<MultipartFile> productImages,
            @RequestPart(required = false) List<MultipartFile> productDetailImages) {
        productColorCommandService.createProductColor(
                requestDto,
                thumbnailImage,
                productImages,
                productDetailImages,
                productId
        );
    }

    // 상품 색상 수정
    @PutMapping("/{productColorId}")
    public void updateProductColor(
            @PathVariable Long productColorId,
            @RequestPart ProductColorUpdateRequestDto requestDto,
            @RequestPart(required = false) MultipartFile thumbnailImage,
            @RequestPart(required = false) List<MultipartFile> newImages,
            @RequestPart(required = false) List<MultipartFile> newDetailImages) {
        productColorCommandService.updateProductColor(
                productColorId,
                requestDto,
                thumbnailImage,
                newImages,
                newDetailImages
        );
    }

    // 상품 색상 삭제
    @DeleteMapping("/{productColorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductColor(@PathVariable Long productColorId) {
        productColorCommandService.deleteProductColor(productColorId);
    }
}

