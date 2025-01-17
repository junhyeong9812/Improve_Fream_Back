package Fream_back.improve_Fream_Back.order.entity;

public enum OrderStatus {
    PENDING_PAYMENT, // 결제 대기
    PAYMENT_COMPLETED, // 결제 완료
    PREPARING, // 상품 준비 중
    IN_WAREHOUSE, // 창고 보관 중
    SHIPMENT_STARTED, // 배송 시작
    IN_TRANSIT, // 배송 중
    COMPLETED, // 배송 완료
    REFUND_REQUESTED, // 환불 대기
    REFUNDED; // 환불 완료

    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING_PAYMENT:
                return newStatus == PAYMENT_COMPLETED || newStatus == COMPLETED;
            case PAYMENT_COMPLETED:
                return newStatus == PREPARING || newStatus == REFUND_REQUESTED;
            case PREPARING:
                return newStatus == IN_WAREHOUSE || newStatus == SHIPMENT_STARTED;
            case IN_WAREHOUSE:
                return newStatus == SHIPMENT_STARTED; // 창고 보관에서 배송 시작 가능
            case SHIPMENT_STARTED:
                return newStatus == IN_TRANSIT;
            case IN_TRANSIT:
                return newStatus == COMPLETED;
            case REFUND_REQUESTED:
                return newStatus == REFUNDED;
            default:
                return false;
        }
    }
}
