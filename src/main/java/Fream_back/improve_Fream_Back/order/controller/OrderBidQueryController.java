package Fream_back.improve_Fream_Back.order.controller;

import Fream_back.improve_Fream_Back.order.dto.OrderBidResponseDto;
import Fream_back.improve_Fream_Back.order.service.OrderBidQueryService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderBidQueryController {
    private final OrderBidQueryService orderBidQueryService;

    @GetMapping("/api/order-bids")
    public Page<OrderBidResponseDto> getOrderBids(
            @RequestParam(required = false) String bidStatus,
            @RequestParam(required = false) String orderStatus,
            Pageable pageable
    ) {
        // Security Context에서 이메일 추출
        String email = SecurityUtils.extractEmailFromSecurityContext();

        // 이메일을 포함한 검색 조건 처리
        return orderBidQueryService.getOrderBids(email, bidStatus, orderStatus, pageable);
    }
}
