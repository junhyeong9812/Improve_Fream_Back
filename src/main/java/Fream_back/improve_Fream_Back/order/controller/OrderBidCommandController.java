package Fream_back.improve_Fream_Back.order.controller;

import Fream_back.improve_Fream_Back.order.dto.InstantOrderRequestDto;
import Fream_back.improve_Fream_Back.order.dto.OrderBidRequestDto;
import Fream_back.improve_Fream_Back.order.entity.OrderBid;
import Fream_back.improve_Fream_Back.order.service.OrderBidCommandService;
import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-bids")
@RequiredArgsConstructor
public class OrderBidCommandController {

    private final OrderBidCommandService orderBidCommandService;

    // OrderBid 생성
    @PostMapping
    public ResponseEntity<Long> createOrderBid(
            @RequestBody OrderBidRequestDto requestDto
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 이메일 추출
        Long orderBidId = orderBidCommandService.createOrderBid(
                email,
                requestDto.getProductSizeId(),
                requestDto.getBidPrice()
        ).getId();
        return ResponseEntity.ok(orderBidId);
    }

    // OrderBid 삭제
    @DeleteMapping("/{orderBidId}")
    public ResponseEntity<Void> deleteOrderBid(@PathVariable("orderBidId") Long orderBidId) {
        orderBidCommandService.deleteOrderBid(orderBidId);
        return ResponseEntity.ok().build();
    }
    // 즉시 구매 입찰 생성
    @PostMapping("/instant")
    public ResponseEntity<Long> createInstantOrderBid(
            @RequestBody InstantOrderRequestDto requestDto
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext(); // 이메일 추출
        Long orderId = orderBidCommandService.createInstantOrderBid(
                email,
                requestDto.getSaleBidId(),
                requestDto.getAddressId(),
                requestDto.isWarehouseStorage(),
                enrichPaymentRequest(requestDto.getPaymentRequest(), email) // 수정된 구조 반영
        ).getId();

        return ResponseEntity.ok(orderId);
    }

    private PaymentRequestDto enrichPaymentRequest(PaymentRequestDto paymentRequest, String email) {
        paymentRequest.setUserEmail(email);
        paymentRequest.setOrderId(null); // 초기에는 null로 설정, 이후 Order 생성 후 업데이트 가능
        return paymentRequest;
    }
}
