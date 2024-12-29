package Fream_back.improve_Fream_Back.sale.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstantSaleRequestDto {

    private Long orderBidId; // 주문 입찰 ID
    private String returnAddress; // 반송 주소
    private String postalCode; // 우편번호
    private String receiverPhone; // 수령인 전화번호
}

