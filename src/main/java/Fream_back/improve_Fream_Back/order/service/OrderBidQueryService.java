package Fream_back.improve_Fream_Back.order.service;

import Fream_back.improve_Fream_Back.order.dto.OrderBidResponseDto;
import Fream_back.improve_Fream_Back.order.entity.OrderBid;
import Fream_back.improve_Fream_Back.order.repository.OrderBidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderBidQueryService {

    private final OrderBidRepository orderBidRepository;

    public Optional<OrderBid> findById(Long id) {
        return orderBidRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public Optional<OrderBid> findByOrderId(Long orderId) {
        return orderBidRepository.findByOrderId(orderId);
    }
    public Page<OrderBidResponseDto> getOrderBids(String email, String bidStatus, String orderStatus, Pageable pageable) {
        return orderBidRepository.findOrderBidsByFilters(email, bidStatus, orderStatus, pageable);
    }
}
