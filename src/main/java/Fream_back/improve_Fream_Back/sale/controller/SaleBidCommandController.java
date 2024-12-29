package Fream_back.improve_Fream_Back.sale.controller;

import Fream_back.improve_Fream_Back.sale.dto.SaleBidRequestDto;
import Fream_back.improve_Fream_Back.sale.service.SaleBidCommandService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale-bids")
@RequiredArgsConstructor
public class SaleBidCommandController {

    private final SaleBidCommandService saleBidCommandService;

    // 판매 입찰 생성
    @PostMapping
    public ResponseEntity<Long> createSaleBid(@RequestBody SaleBidRequestDto requestDto) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 이메일 추출
        Long saleBidId = saleBidCommandService.createSaleBid(
                email,
                requestDto.getProductSizeId(),
                requestDto.getBidPrice(),
                requestDto.getReturnAddress(),
                requestDto.getPostalCode(),
                requestDto.getReceiverPhone(),
                requestDto.isWarehouseStorage()
        ).getId();
        return ResponseEntity.ok(saleBidId);
    }

    // 판매 입찰 삭제
    @DeleteMapping("/{saleBidId}")
    public ResponseEntity<Void> deleteSaleBid(@PathVariable Long saleBidId) {
        saleBidCommandService.deleteSaleBid(saleBidId);
        return ResponseEntity.ok().build();
    }
}

