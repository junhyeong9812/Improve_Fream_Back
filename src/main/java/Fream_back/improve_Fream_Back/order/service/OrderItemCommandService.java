package Fream_back.improve_Fream_Back.order.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderItemRepository;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemCommandService {

    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderItem createOrderItem(Order order, ProductSize productSize, int price) {
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productSize(productSize)
                .quantity(1) // 주문 수량은 기본 1
                .price(price)
                .build();

        return orderItemRepository.save(orderItem);
    }
}
