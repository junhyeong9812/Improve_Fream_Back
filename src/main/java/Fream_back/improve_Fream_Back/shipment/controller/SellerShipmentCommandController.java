package Fream_back.improve_Fream_Back.shipment.controller;

import Fream_back.improve_Fream_Back.shipment.dto.SellerShipmentRequestDto;
import Fream_back.improve_Fream_Back.shipment.service.SellerShipmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments/seller")
@RequiredArgsConstructor
public class SellerShipmentCommandController {

    private final SellerShipmentCommandService sellerShipmentCommandService;

    // 배송 정보 생성 엔드포인트
    @PostMapping
    public ResponseEntity<Long> createSellerShipment(
            @RequestBody SellerShipmentRequestDto requestDto
    ) {
        Long shipmentId = sellerShipmentCommandService.createSellerShipment(
                requestDto.getSaleId(),
                requestDto.getCourier(),
                requestDto.getTrackingNumber()
        ).getId();
        return ResponseEntity.ok(shipmentId);
    }

    // 배송 정보 업데이트 엔드포인트
    @PatchMapping("/{shipmentId}")
    public ResponseEntity<Void> updateShipment(
            @PathVariable Long shipmentId,
            @RequestBody SellerShipmentRequestDto requestDto
    ) {
        sellerShipmentCommandService.updateShipment(
                shipmentId,
                requestDto.getCourier(),
                requestDto.getTrackingNumber()
        );
        return ResponseEntity.ok().build();
    }
}