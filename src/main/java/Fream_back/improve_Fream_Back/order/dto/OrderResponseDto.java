package Fream_back.improve_Fream_Back.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private List<OrderItemResponseDto> orderItems;
    private String shipmentStatus;
    private String trackingNumber;
    private String courierCompany;
    private String phoneNumber;
    private boolean paymentCompleted;
    private String paymentMethod;
    private BigDecimal paymentAmount;
    private String recipientName;
    private String address;
    private String addressDetail;
    private String zipCode;
    private BigDecimal totalPrice;
}
