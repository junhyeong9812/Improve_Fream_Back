package Fream_back.improve_Fream_Back.sale.controller;

import Fream_back.improve_Fream_Back.sale.dto.SaleBidResponseDto;
import Fream_back.improve_Fream_Back.sale.dto.SaleBidStatusCountDto;
import Fream_back.improve_Fream_Back.sale.service.SaleBidQueryService;
import Fream_back.improve_Fream_Back.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sale-bids") // 공통 경로를 클래스 레벨로 이동
@RequiredArgsConstructor
public class SaleBidQueryController {

    private final SaleBidQueryService saleBidQueryService;

    // SaleBid 목록 조회
    @GetMapping
    public Page<SaleBidResponseDto> getSaleBids(
            @RequestParam(value = "saleBidStatus", required = false) String saleBidStatus,
            @RequestParam(value = "saleStatus", required = false) String saleStatus,
            Pageable pageable
    ) {
        String email = SecurityUtils.extractEmailFromSecurityContext();
        return saleBidQueryService.getSaleBids(email, saleBidStatus, saleStatus, pageable);
    }

    // SaleBid 상태 카운트 조회
    @GetMapping("/count")
    public SaleBidStatusCountDto getSaleBidStatusCounts() {
        String email = SecurityUtils.extractEmailFromSecurityContext();
        return saleBidQueryService.getSaleBidStatusCounts(email);
    }
}
