package Fream_back.improve_Fream_Back.shipment.dto;

import Fream_back.improve_Fream_Back.shipment.entity.ShipmentStatus;

public class ShipmentStatusResponse {
    private ShipmentStatus status;
    public ShipmentStatusResponse(ShipmentStatus status) {
        this.status = status;
    }
    public ShipmentStatus getStatus() { return status; }
}
