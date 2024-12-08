package Fream_back.improve_Fream_Back.order.dto;

import Fream_back.improve_Fream_Back.payment.dto.PaymentDetailsDto;
import Fream_back.improve_Fream_Back.shipment.dto.ShipmentResponseDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderDetailsDto {
    private Long orderId;
    private Long userId;
    private String recipientName;
    private String phoneNumber;
    private String address;
    private String addressDetail;
    private String zipCode;
    private BigDecimal totalPrice;
    private boolean paymentCompleted;
    private PaymentDetailsDto paymentDetails; // 결제 상세 정보
    private ShipmentResponseDto shipmentDetails; // 배송 상세 정보
    private List<OrderItemResponseDto> orderItems; // 주문 상품 정보
}
