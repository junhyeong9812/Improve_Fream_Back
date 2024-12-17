package Fream_back.improve_Fream_Back.order.service;

import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import Fream_back.improve_Fream_Back.delivery.repository.DeliveryRepository;
import Fream_back.improve_Fream_Back.order.dto.OrderCreateRequestDto;
import Fream_back.improve_Fream_Back.order.dto.OrderDetailsDto;
import Fream_back.improve_Fream_Back.order.dto.OrderItemResponseDto;
import Fream_back.improve_Fream_Back.order.dto.OrderResponseDto;
import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.entity.OrderItem;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.payment.dto.PaymentDetailsDto;
import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
import Fream_back.improve_Fream_Back.payment.dto.PaymentResponseDto;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.service.PaymentService;
import Fream_back.improve_Fream_Back.shipment.dto.ShipmentResponseDto;
import Fream_back.improve_Fream_Back.shipment.entity.Shipment;
import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;
import Fream_back.improve_Fream_Back.shipment.service.ShipmentService;
import Fream_back.improve_Fream_Back.user.entity.User;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentService paymentService;
    private final ShipmentService shipmentService;
    private final ProductRepository productRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderResponseDto createOrder(OrderCreateRequestDto requestDto) {
        // 1. 유저 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 사용자 주소지 검색
        Delivery delivery = deliveryRepository.findById(requestDto.getDeliveryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 배송지를 찾을 수 없습니다."));

        // 배송지가 사용자의 것인지 검증
        if (!delivery.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("사용자와 배송지가 일치하지 않습니다.");
        }

        // 3. OrderItem 리스트 생성
        List<OrderItem> orderItems = requestDto.getOrderItems().stream()
                .map(dto -> {
                    Product product = productRepository.findById(dto.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("상품 정보를 찾을 수 없습니다."));
                    return OrderItem.builder()
                            .product(product)
                            .quantity(dto.getQuantity())
                            .price(dto.getPrice()) // 상품 가격 사용
                            .build();
                })
                .toList();

        // 4. 주문 생성
        Order order = Order.createOrderFromDelivery(user, delivery, orderItems);
        orderRepository.save(order);

        // 5. 결제 초기화
        Payment payment = paymentService.createPayment(order);

        // 6. Response 변환
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .addressDetail(order.getAddressDetail())
                .zipCode(order.getZipCode())
                .totalPrice(order.getTotalPrice())
                .paymentId(payment.getId()) // 생성된 결제 ID 포함
                .orderItems(orderItems.stream()
                        .map(orderItem -> OrderItemResponseDto.builder()
                                .productId(orderItem.getProduct().getId())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build())
                        .toList())
                .build();
    }


    /**
     * 주문 결제 완료 처리 (결제 생성 → 결제 성공 → 배송 준비 상태로 변경)
     */
    @Transactional
    public void processPaymentAndShipment(Long orderId, Long paymentId, PaymentRequestDto paymentRequestDto) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 2. 중복 결제 방지
        if (order.isPaymentCompleted()) {
            throw new IllegalStateException("이미 결제가 완료된 주문입니다.");
        }

        // 3. 결제 정보 업데이트
        PaymentResponseDto paymentResponse = paymentService.updatePaymentDetails(paymentId, paymentRequestDto);

        // 4. 결제 성공 여부 확인
        if (!paymentResponse.isSuccessful()) {
            throw new IllegalStateException("결제가 실패했습니다.");
        }

        // 5. 주문 결제 완료 상태 업데이트
        order.markPaymentCompleted();

        // 6. 배송 준비 상태로 Shipment 생성
        shipmentService.createShipment(order);
    }

    /**
     * 주문 상세 조회
     */
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findOrderDetailsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .addressDetail(order.getAddressDetail())
                .zipCode(order.getZipCode())
                .totalPrice(order.getTotalPrice())
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemResponseDto.builder()
                                .productId(orderItem.getProduct().getId())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build())
                        .toList())
                .build();
    }

    /**
     * 필터링된 주문 목록 조회
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getFilteredOrders(Long userId, String shipmentStatus, boolean includePayments) {
        ShipmentStatus status = shipmentStatus != null ? ShipmentStatus.valueOf(shipmentStatus.toUpperCase()) : null;
        List<Order> orders = orderRepository.findOrdersWithDynamicFilters(userId, status, includePayments);

        return orders.stream()
                .map(order -> OrderResponseDto.builder()
                        .orderId(order.getId())
                        .userId(order.getUser().getId())
                        .recipientName(order.getRecipientName())
                        .phoneNumber(order.getPhoneNumber())
                        .address(order.getAddress())
                        .addressDetail(order.getAddressDetail())
                        .zipCode(order.getZipCode())
                        .totalPrice(order.getTotalPrice())
                        .orderItems(order.getOrderItems().stream()
                                .map(orderItem -> OrderItemResponseDto.builder()
                                        .productId(orderItem.getProduct().getId())
                                        .quantity(orderItem.getQuantity())
                                        .price(orderItem.getPrice())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrderWithRefund(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        Shipment shipment = order.getShipment();

        if (shipment == null) {
            throw new IllegalStateException("배송 정보가 없는 주문입니다.");
        }

        // 배송 상태 확인
        if (shipment.getShipmentStatus() == ShipmentStatus.PENDING) {
            // 배송 준비 중 -> 즉시 환불 처리
            paymentService.refundPayment(order.getPayment().getId());
            shipment.updateShipmentStatus(ShipmentStatus.CANCELED);
        } else if (shipment.getShipmentStatus() == ShipmentStatus.SHIPPED ||
                shipment.getShipmentStatus() == ShipmentStatus.IN_TRANSIT ||
                shipment.getShipmentStatus() == ShipmentStatus.OUT_FOR_DELIVERY ||
                shipment.getShipmentStatus() == ShipmentStatus.DELIVERED) {
            // 배송 진행 중 또는 완료 -> 환불 대기 상태로 변경
            shipment.updateShipmentStatus(ShipmentStatus.REFUND_PENDING);
        } else {
            throw new IllegalStateException("현재 상태에서는 주문을 취소할 수 없습니다.");
        }

        order.markPaymentCompleted(false); // 결제 완료 상태 해제
    }

    //환불 대기 상태 처리
    @Transactional
    public void processRefundForPendingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        Shipment shipment = order.getShipment();

        if (shipment == null || shipment.getShipmentStatus() != ShipmentStatus.REFUND_PENDING) {
            throw new IllegalStateException("환불 대기 상태가 아닌 주문입니다.");
        }

        // 결제 환불 처리
        paymentService.refundPayment(order.getPayment().getId());

        // 배송 상태를 환불 완료로 변경
        shipment.updateShipmentStatus(ShipmentStatus.CANCELED);

        // 주문 상태 업데이트
        order.markPaymentCompleted(false);
    }

    //환불 대기 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getRefundPendingOrders(Long userId) {
        List<Order> orders = orderRepository.findOrdersByUserIdAndShipmentStatus(userId, ShipmentStatus.REFUND_PENDING);

        return orders.stream()
                .map(order -> OrderResponseDto.builder()
                        .orderId(order.getId())
                        .userId(order.getUser().getId())
                        .recipientName(order.getRecipientName())
                        .phoneNumber(order.getPhoneNumber())
                        .address(order.getAddress())
                        .addressDetail(order.getAddressDetail())
                        .zipCode(order.getZipCode())
                        .totalPrice(order.getTotalPrice())
                        .paymentCompleted(order.isPaymentCompleted())
                        .shipmentStatus(order.getShipment().getShipmentStatus().name())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailsDto getOrderDetailsWithPaymentAndShipment(Long orderId) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다."));

        // 결제 정보 가져오기
        Payment payment = order.getPayment();

        // 배송 정보 가져오기
        Shipment shipment = order.getShipment();

        // OrderDetailsDto로 변환
        return OrderDetailsDto.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .addressDetail(order.getAddressDetail())
                .zipCode(order.getZipCode())
                .totalPrice(order.getTotalPrice())
                .paymentCompleted(order.isPaymentCompleted())
                .paymentDetails(payment != null ? PaymentDetailsDto.builder()
                        .paymentId(payment.getId())
                        .impUid(payment.getImpUid())
                        .merchantUid(payment.getMerchantUid())
                        .payMethod(payment.getPayMethod())
                        .paidAmount(payment.getPaidAmount())
                        .isSuccessful(payment.isSuccess())
                        .isRefunded(payment.isRefunded())
                        .cancelledAt(payment.getCancelledAt())
                        .buyerName(payment.getBuyerName())
                        .buyerEmail(payment.getBuyerEmail())
                        .buyerTel(payment.getBuyerTel())
                        .buyerAddr(payment.getBuyerAddr())
                        .buyerPostcode(payment.getBuyerPostcode())
                        .status(payment.getStatus())
                        .paidAt(payment.getPaidAt())
                        .build() : null)
                .shipmentDetails(shipment != null ? ShipmentResponseDto.builder()
                        .shipmentId(shipment.getId())
                        .shipmentStatus(shipment.getShipmentStatus().name())
                        .trackingNumber(shipment.getTrackingNumber())
                        .courierCompany(shipment.getCourierCompany())
                        .shippedAt(shipment.getShippedAt())
                        .deliveredAt(shipment.getDeliveredAt())
                        .build() : null)
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemResponseDto.builder()
                                .productId(orderItem.getProduct().getId())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build())
                        .toList())
                .build();
    }
}
