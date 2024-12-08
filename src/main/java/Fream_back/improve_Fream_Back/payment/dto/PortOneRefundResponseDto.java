package Fream_back.improve_Fream_Back.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortOneRefundResponseDto {
    private String code; // 응답 코드
    private String message; // 응답 메시지
    private RefundResponse response;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundResponse {
        private String impUid; // PortOne 거래 고유번호
        private String merchantUid; // 상점 주문 번호
        private double cancelAmount; // 취소된 금액
        private String status; // 상태
        private long cancelledAt; // 취소 시각 (Unix timestamp)
    }

    public boolean isSuccess() {
        return "0".equals(code);
    }
}
