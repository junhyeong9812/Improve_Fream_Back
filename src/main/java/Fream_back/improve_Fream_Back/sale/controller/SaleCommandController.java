package Fream_back.improve_Fream_Back.sale.controller;

import Fream_back.improve_Fream_Back.sale.dto.InstantSaleRequestDto;
import Fream_back.improve_Fream_Back.sale.dto.ShipmentRequestDto;
import Fream_back.improve_Fream_Back.sale.service.SaleCommandService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleCommandController {

    private final SaleCommandService saleCommandService;

    // 즉시 판매 생성
//    @PostMapping("/instant")
//    public ResponseEntity<Long> createInstantSale(@RequestBody InstantSaleRequestDto requestDto) {
//        // SecurityUtils를 통해 이메일 추출
//        String sellerEmail = SecurityUtils.extractEmailFromSecurityContext();
//
//        Long saleId = saleCommandService.createInstantSale(
//                requestDto.getOrderBidId(),
//                sellerEmail,
//                requestDto.getReturnAddress(),
//                requestDto.getPostalCode(),
//                requestDto.getReceiverPhone()
//        ).getId();
//        return ResponseEntity.ok(saleId);
//    }

    // 판매 상태 업데이트 (배송 상태)
    @PostMapping("/{saleId}/shipment")
    public ResponseEntity<Void> createSellerShipment(
            @PathVariable("saleId") Long saleId,
            @RequestBody ShipmentRequestDto requestDto
    ) {
        saleCommandService.createSellerShipment(
                saleId,
                requestDto.getCourier(),
                requestDto.getTrackingNumber()
        );
        return ResponseEntity.ok().build();
    }
}
