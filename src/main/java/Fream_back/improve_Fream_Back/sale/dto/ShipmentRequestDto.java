package Fream_back.improve_Fream_Back.sale.dto;

import lombok.*;

@Data
@Builder
public class ShipmentRequestDto {
    private String courier; // 택배사
    private String trackingNumber; // 운송장 번호
}
