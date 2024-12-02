package Fream_back.improve_Fream_Back.order.dto;

import lombok.Data;

@Data
public class ShipmentUpdateRequestDto {
    private String shipmentStatus;
    private String trackingNumber;
    private String courierCompany;
}
