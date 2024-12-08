package Fream_back.improve_Fream_Back.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundRequestDto {
    private double amount; // 환불 금액
    private String reason; // 환불 사유
    private String refundHolder; // 환불 계좌 예금주
    private String refundBank; // 환불 계좌 은행코드
    private String refundAccount; // 환불 계좌 번호
}
