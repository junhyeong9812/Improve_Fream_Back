package Fream_back.improve_Fream_Back.order.service;

import Fream_back.improve_Fream_Back.order.entity.BidStatus;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderBid;
import Fream_back.improve_Fream_Back.order.repository.OrderBidRepository;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.product.service.productSize.ProductSizeQueryService;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderBidCommandService {

    private final OrderBidRepository orderBidRepository;
    private final OrderCommandService orderCommandService;
    private final ProductSizeQueryService productSizeQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public OrderBid createOrderBid(String email, Long productSizeId, int bidPrice) {
        // 1. User 조회
        User user = userQueryService.findByEmail(email);

        // 2. ProductSize 조회
        ProductSize productSize = productSizeQueryService.findById(productSizeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사이즈를 찾을 수 없습니다."));

        // 3. Order 생성
        Order order = orderCommandService.createOrderFromBid(user, productSize, bidPrice);

        // 4. OrderBid 생성
        OrderBid orderBid = OrderBid.builder()
                .user(user)
                .productSize(productSize)
                .bidPrice(bidPrice)
                .status(BidStatus.PENDING)
                .order(order) // Order 매핑
                .build();
        // 양방향 관계 설정
        orderBid.assignOrder(order);

        // 5. OrderBid 저장
        return orderBidRepository.save(orderBid);
    }
    @Transactional
    public void matchOrderBid(OrderBid orderBid) {
        orderBid.updateStatus(BidStatus.MATCHED);
    }
    @Transactional
    public void deleteOrderBid(Long orderBidId) {
        // OrderBid 조회
        OrderBid orderBid = orderBidRepository.findById(orderBidId)
                .orElseThrow(() -> new IllegalArgumentException("해당 OrderBid를 찾을 수 없습니다: " + orderBidId));

        // Sale과 연결 여부 확인
        if (orderBid.getSale() != null) {
            throw new IllegalStateException("OrderBid는 Sale과 연결되어 있으므로 삭제할 수 없습니다.");
        }

        // Order와 연결 여부 확인 후 삭제
        if (orderBid.getOrder() != null) {
            orderCommandService.deleteOrder(orderBid.getOrder().getId());
        }

        // OrderBid 삭제
        orderBidRepository.delete(orderBid);
    }
}
