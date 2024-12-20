//package Fream_back.improve_Fream_Back.order.controller;
//
//import Fream_back.improve_Fream_Back.order.dto.OrderCreateRequestDto;
//import Fream_back.improve_Fream_Back.order.dto.OrderDetailsDto;
//import Fream_back.improve_Fream_Back.order.dto.OrderResponseDto;
//import Fream_back.improve_Fream_Back.order.service.OrderService;
//import Fream_back.improve_Fream_Back.payment.dto.PaymentDetailsDto;
//import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
//import Fream_back.improve_Fream_Back.payment.dto.PaymentResponseDto;
//import Fream_back.improve_Fream_Back.shipment.dto.ShipmentResponseDto;
//import Fream_back.improve_Fream_Back.shipment.dto.ShipmentUpdateRequestDto;
//import Fream_back.improve_Fream_Back.shipment.service.ShipmentService;
//import Fream_back.improve_Fream_Back.payment.service.PaymentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@RestController
//@RequestMapping("/order")
//@RequiredArgsConstructor
//public class OrderController {
//
//    private final OrderService orderService;
//    private final PaymentService paymentService;
//    private final ShipmentService shipmentService;
//
//    /**
//     * 주문 생성
//     */
//    @PostMapping
//    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateRequestDto requestDto) {
//        OrderResponseDto responseDto = orderService.createOrder(requestDto);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    /**
//     * 주문 상세 조회
//     */
//    @GetMapping("/{orderId}")
//    public ResponseEntity<OrderDetailsDto> getOrderDetails(@PathVariable("orderId") Long orderId) {
//        OrderDetailsDto responseDto = orderService.getOrderDetailsWithPaymentAndShipment(orderId);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    /**
//     * 주문 결제 완료 및 배송 준비 상태로 업데이트
//     */
//    @PostMapping("/{orderId}/complete-payment")
//    public ResponseEntity<String> completeOrderPayment(
//            @PathVariable("orderId") Long orderId,
//            @RequestParam Long paymentId,
//            @RequestBody PaymentRequestDto paymentRequestDto
//    ) {
//        orderService.processPaymentAndShipment(orderId, paymentId, paymentRequestDto);
//        return ResponseEntity.ok("Order payment completed and shipment created.");
//    }
//
//    /**
//     * 배송 상태 업데이트
//     */
//    @PutMapping("/{orderId}/shipment")
//    public ResponseEntity<String> updateShipmentStatus(
//            @PathVariable("orderId") Long orderId,
//            @RequestBody ShipmentUpdateRequestDto requestDto
//    ) {
//        shipmentService.updateShipmentStatus(orderId, requestDto);
//        return ResponseEntity.ok("Shipment status updated successfully.");
//    }
//
//    /**
//     * 결제 환불 처리
//     */
//    @PostMapping("/{orderId}/refund")
//    public ResponseEntity<String> refundOrder(@PathVariable("orderId") Long orderId) {
//        orderService.cancelOrderWithRefund(orderId);
//        return ResponseEntity.ok("Order refund initiated successfully.");
//    }
//
//    /**
//     * 환불 대기 상태의 주문 처리
//     */
//    @PostMapping("/{orderId}/process-refund")
//    public ResponseEntity<String> processPendingRefund(@PathVariable("orderId") Long orderId) {
//        orderService.processRefundForPendingOrder(orderId);
//        return ResponseEntity.ok("Pending refund processed successfully.");
//    }
//
//    /**
//     * 환불 대기 주문 목록 조회
//     */
//    @GetMapping("/user/{userId}/refund-pending")
//    public ResponseEntity<List<OrderResponseDto>> getRefundPendingOrders(
//            @PathVariable("userId") Long userId
//    ) {
//        List<OrderResponseDto> orders = orderService.getRefundPendingOrders(userId);
//        return ResponseEntity.ok(orders);
//    }
//
//    /**
//     * 주문 목록 조회 (필터링 지원)
//     */
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<OrderResponseDto>> getOrdersWithFilters(
//            @PathVariable("userId") Long userId,
//            @RequestParam(required = false) String shipmentStatus,
//            @RequestParam(defaultValue = "false") boolean includePayments
//    ) {
//        List<OrderResponseDto> orders = orderService.getFilteredOrders(userId, shipmentStatus, includePayments);
//        return ResponseEntity.ok(orders);
//    }
//
//    /**
//     * 특정 주문의 결제 정보 조회
//     */
//    @GetMapping("/{orderId}/payment")
//    public ResponseEntity<PaymentDetailsDto> getPaymentDetails(@PathVariable("orderId") Long orderId) {
//        PaymentDetailsDto responseDto = paymentService.getPaymentDetailsByOrder(orderId);
//        return ResponseEntity.ok(responseDto);
//    }
//
//    /**
//     * 특정 주문의 배송 정보 조회
//     */
//    @GetMapping("/{orderId}/shipment")
//    public ResponseEntity<ShipmentResponseDto> getShipmentDetails(@PathVariable("orderId") Long orderId) {
//        ShipmentResponseDto responseDto = shipmentService.getShipmentDetailsByOrder(orderId);
//        return ResponseEntity.ok(responseDto);
//    }
//}