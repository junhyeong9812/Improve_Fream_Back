package Fream_back.improve_Fream_Back.order.service;

import Fream_back.improve_Fream_Back.order.dto.PayAndShipmentRequestDto;
import Fream_back.improve_Fream_Back.order.entity.*;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.service.PaymentCommandService;
import Fream_back.improve_Fream_Back.product.entity.ProductSize;
import Fream_back.improve_Fream_Back.shipment.entity.OrderShipment;
import Fream_back.improve_Fream_Back.shipment.service.OrderShipmentCommandService;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final PaymentCommandService paymentCommandService;
    private final OrderShipmentCommandService orderShipmentCommandService;
    private final OrderItemCommandService orderItemCommandService;
    private final OrderBidQueryService orderBidQueryService;
    private final UserQueryService userQueryService;

    @Transactional
    public Order createOrderFromBid(User user, ProductSize productSize, int bidPrice) {
        // 1. OrderItem 생성
        OrderItem orderItem = orderItemCommandService.createOrderItem(null, productSize, bidPrice);

        // 2. Order 생성
        Order order = Order.builder()
                .user(user)
                .totalAmount(bidPrice) // 입찰가를 총 금액으로 설정
                .discountAmount(0) // 초기값은 0
                .usedPoints(0) // 초기값은 0
                .status(OrderStatus.PENDING_PAYMENT) // 결제 대기 상태
                .build();

        // 3. OrderItem과 연관 설정
        order.addOrderItem(orderItem);

        // 4. Order 저장
        return orderRepository.save(order);
    }
    @Transactional
    public void processPaymentAndShipment(Long orderId, String userEmail, PayAndShipmentRequestDto requestDto) {
        // 1. User 및 Order 조회
        User user = userQueryService.findByEmail(userEmail);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 Order를 찾을 수 없습니다: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("해당 사용자는 이 Order를 처리할 권한이 없습니다.");
        }

        // 2. 결제 처리
        Payment payment = paymentCommandService.processPayment(order, user, requestDto.getPaymentRequest());
        order.assignPayment(payment);

        // 3. 배송 정보 생성
        OrderShipment shipment = orderShipmentCommandService.createOrderShipment(
                order,
                requestDto.getReceiverName(),
                requestDto.getReceiverPhone(),
                requestDto.getPostalCode(),
                requestDto.getAddress()
        );
        order.assignOrderShipment(shipment);

        // 4. 상태 업데이트
        order.updateStatus(OrderStatus.PAYMENT_COMPLETED);
        order.updateStatus(OrderStatus.PREPARING);

        // 5. OrderBid 상태 업데이트
        orderBidQueryService.findById(orderId).ifPresent(orderBid -> {
            orderBid.updateStatus(BidStatus.MATCHED);
        });
    }


}
