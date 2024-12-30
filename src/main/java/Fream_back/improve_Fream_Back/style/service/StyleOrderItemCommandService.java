package Fream_back.improve_Fream_Back.style.service;

import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.service.OrderItemQueryService;
import Fream_back.improve_Fream_Back.style.entity.Style;
import Fream_back.improve_Fream_Back.style.entity.StyleOrderItem;
import Fream_back.improve_Fream_Back.style.repository.StyleOrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StyleOrderItemCommandService {

    private final StyleOrderItemRepository styleOrderItemRepository;
    private final OrderItemQueryService orderItemQueryService;

    public StyleOrderItem createStyleOrderItem(Long orderItemId, Style style) {
        // 1. OrderItem 조회
        OrderItem orderItem = orderItemQueryService.findById(orderItemId);

        // 2. StyleOrderItem 생성
        StyleOrderItem styleOrderItem = StyleOrderItem.builder()
                .style(style)
                .orderItem(orderItem)
                .build();

        // 3. 저장
        return styleOrderItemRepository.save(styleOrderItem);
    }
}
