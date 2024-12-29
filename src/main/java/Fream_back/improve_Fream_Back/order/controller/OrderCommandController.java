package Fream_back.improve_Fream_Back.order.controller;

import Fream_back.improve_Fream_Back.order.dto.InstantOrderRequestDto;
import Fream_back.improve_Fream_Back.order.dto.PayAndShipmentRequestDto;
import Fream_back.improve_Fream_Back.order.service.OrderCommandService;
import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    // 즉시 주문 생성
    @PostMapping("/instant")
    public ResponseEntity<Long> createInstantOrder(@RequestBody InstantOrderRequestDto requestDto) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 이메일 추출

        Long orderId = orderCommandService.createInstantOrder(
                email,
                requestDto.getSaleBidId(),
                requestDto.getAddressId(),
                requestDto.isWarehouseStorage(),
                requestDto.getPayAndShipment().getPaymentRequest()
        ).getId();

        return ResponseEntity.ok(orderId);
    }

    // 결제 및 배송 처리
    @PostMapping("/{orderId}/process-payment-shipment")
    public ResponseEntity<Void> processPaymentAndShipment(
            @PathVariable Long orderId,
            @RequestBody PayAndShipmentRequestDto requestDto
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 이메일 추출
        orderCommandService.processPaymentAndShipment(orderId, email, requestDto);
        return ResponseEntity.ok().build();
    }
}
