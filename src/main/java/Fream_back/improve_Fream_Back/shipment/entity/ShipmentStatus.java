package Fream_back.improve_Fream_Back.shipment.entity;

public enum ShipmentStatus {

    PENDING,       // 배송 준비 중
    SHIPPED,       // 배송 시작 (운송장 번호가 등록된 상태)
    IN_TRANSIT,    // 배송 중
    OUT_FOR_DELIVERY, // 배달 중
    DELIVERED,     // 배송 완료
    RETURNED,      // 반송 처리됨
    CANCELED,       // 배송 취소됨
    REFUND_PENDING // 환불 대기 중
}