package Fream_back.improve_Fream_Back.product.controller;

import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-sizes")
@RequiredArgsConstructor
public class ProductSizeController {

    private final ProductSizeCommandService productSizeCommandService;

    /**
     * ProductSize 업데이트
     * @param sizeId 사이즈 ID
     * @param purchasePrice 새로운 구매 가격
     * @param salePrice 새로운 판매 가격
     * @param quantity 새로운 수량
     */
    @PutMapping("/{sizeId}")
    public ResponseEntity<String> updateProductSize(
            @PathVariable Long sizeId,
            @RequestParam int purchasePrice,
            @RequestParam int salePrice,
            @RequestParam int quantity) {
        productSizeCommandService.updateProductSize(sizeId, purchasePrice, salePrice, quantity);
        return ResponseEntity.ok("Product size updated successfully.");
    }
}

