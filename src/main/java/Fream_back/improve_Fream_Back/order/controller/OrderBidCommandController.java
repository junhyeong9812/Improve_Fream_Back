package Fream_back.improve_Fream_Back.order.controller;

import Fream_back.improve_Fream_Back.order.dto.OrderBidRequestDto;
import Fream_back.improve_Fream_Back.order.entity.OrderBid;
import Fream_back.improve_Fream_Back.order.service.OrderBidCommandService;
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
    public ResponseEntity<Void> deleteOrderBid(@PathVariable Long orderBidId) {
        orderBidCommandService.deleteOrderBid(orderBidId);
        return ResponseEntity.ok().build();
    }
}
