package Fream_back.improve_Fream_Back.shipment.controller;

import Fream_back.improve_Fream_Back.shipment.dto.OrderShipmentRequestDto;
import Fream_back.improve_Fream_Back.shipment.dto.ShipmentStatusResponse;
import Fream_back.improve_Fream_Back.shipment.dto.TrackingNumberRequestDto;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
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

    /**
     * 단건 조회 & 상태 즉시 업데이트 후 결과 반환
     */
    @PostMapping("/{shipmentId}/check-status")
    public ResponseEntity<ShipmentStatusResponse> updateAndCheckStatus(
            @PathVariable("shipmentId") Long shipmentId,
            @RequestBody OrderShipmentRequestDto requestDto
    ) {
        try {
            // 1) Service에서 상태 업데이트 & 조회
            ShipmentStatus updatedStatus = orderShipmentCommandService.updateAndCheckShipmentStatus(
                    shipmentId,
                    requestDto.getCourier(),
                    requestDto.getTrackingNumber()
            );

            // 2) 응답 DTO로 감싸서 반환
            ShipmentStatusResponse response = new ShipmentStatusResponse(updatedStatus);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
