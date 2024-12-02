package Fream_back.improve_Fream_Back.shipment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ShipmentResponseDto {
    private Long shipmentId;
    private String shipmentStatus;
    private String trackingNumber;
    private String courierCompany;
    private LocalDate shippedAt;
    private LocalDate deliveredAt;
}
