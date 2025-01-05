package Fream_back.improve_Fream_Back.order.repository;

import Fream_back.improve_Fream_Back.order.dto.OrderBidResponseDto;
import Fream_back.improve_Fream_Back.order.dto.OrderBidStatusCountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderBidRepositoryCustom {
    Page<OrderBidResponseDto> findOrderBidsByFilters(String email,String bidStatus, String orderStatus, Pageable pageable);
    OrderBidStatusCountDto countOrderBidsByStatus(String email);
    OrderBidResponseDto findOrderBidById(Long orderBidId,String email); // 단일 조회 메서드 추가
}
