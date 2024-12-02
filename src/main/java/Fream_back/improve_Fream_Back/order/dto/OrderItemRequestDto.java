package Fream_back.improve_Fream_Back.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDto {
    private Long productId; // 상품 ID
    private Integer quantity; // 구매 수량
    private BigDecimal price; // 단가
}
