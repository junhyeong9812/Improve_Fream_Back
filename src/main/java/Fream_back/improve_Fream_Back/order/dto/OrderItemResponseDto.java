package Fream_back.improve_Fream_Back.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {
    private Long id; // 주문 항목 ID
    private Long productId;
    private String productName; // 상품명
    private Integer quantity; // 구매 수량
    private BigDecimal price; // 단가
}