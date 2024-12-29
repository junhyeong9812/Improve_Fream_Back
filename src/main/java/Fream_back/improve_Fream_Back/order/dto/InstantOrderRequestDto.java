package Fream_back.improve_Fream_Back.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstantOrderRequestDto {

    private Long saleBidId;                 // 판매 입찰 ID
    private Long addressId;                 // 주소 ID
    private boolean warehouseStorage;       // 창고 보관 여부
    private PayAndShipmentRequestDto payAndShipment; // 결제 및 배송 정보
}
