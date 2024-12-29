package Fream_back.improve_Fream_Back.order.dto;

import lombok.Data;

@Data
public class OrderBidRequestDto {
    private String userEmail;
    private Long productSizeId;
    private int bidPrice;
}
