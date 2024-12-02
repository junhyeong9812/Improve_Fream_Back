package Fream_back.improve_Fream_Back.order.dto;

import Fream_back.improve_Fream_Back.delivery.dto.DeliveryRequestDto;
import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
import Fream_back.improve_Fream_Back.order.dto.OrderItemRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequestDto {
    private Long userId;
    private Long deliveryId; // 기존 배송지 ID
    private List<OrderItemRequestDto> orderItems;
    private DeliveryRequestDto delivery;
    private PaymentRequestDto payment;
}
