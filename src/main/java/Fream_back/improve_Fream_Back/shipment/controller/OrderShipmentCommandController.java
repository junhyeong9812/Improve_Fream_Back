package Fream_back.improve_Fream_Back.shipment.controller;

import Fream_back.improve_Fream_Back.shipment.dto.OrderShipmentRequestDto;
import Fream_back.improve_Fream_Back.shipment.service.OrderShipmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments/order")
@RequiredArgsConstructor
public class OrderShipmentCommandController {

    private final OrderShipmentCommandService orderShipmentCommandService;

    // 배송 상태 업데이트 엔드포인트
    @PatchMapping("/{shipmentId}/status")
    public ResponseEntity<Void> updateTrackingInfo(
            @PathVariable Long shipmentId,
            @RequestBody OrderShipmentRequestDto requestDto
    ) {
        orderShipmentCommandService.updateTrackingInfo(
                shipmentId,
                requestDto.getCourier(),
                requestDto.getTrackingNumber()
        );
        return ResponseEntity.ok().build();
    }
}
