package Fream_back.improve_Fream_Back.shipment.dto;

import lombok.Data;

@Data
public class TrackingNumberRequestDto {
    private String courier;         // 배송사 이름
    private String trackingNumber;  // 송장 번호
}
