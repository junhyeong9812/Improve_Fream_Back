package Fream_back.improve_Fream_Back.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PaymentDetailsDto {
    private Long paymentId;
    private String impUid; // PortOne 고유 ID
    private String merchantUid; // 상점 주문 고유 ID
    private String payMethod; // 결제 수단
    private double paidAmount; // 결제 금액
    private boolean isSuccessful; // 결제 성공 여부
    private boolean isRefunded; // 환불 여부
    private String buyerName; // 구매자 이름
    private String buyerEmail; // 구매자 이메일
    private String buyerTel; // 구매자 전화번호
    private String buyerAddr; // 구매자 주소
    private String buyerPostcode; // 구매자 우편번호
    private long paidAt; // 결제 완료 시간 (Unix timestamp)
    private String status; // 결제 상태
    private LocalDateTime cancelledAt; // 환불 완료 시간
}
