package Fream_back.improve_Fream_Back.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    private String impUid; // PortOne 고유 ID
    private String merchantUid; // 상점 주문 고유 ID
    private String payMethod; // 결제 수단 (카드, 계좌이체 등)
    private double paidAmount; // 결제 금액
    private String buyerName; // 구매자 이름
    private String buyerEmail; // 구매자 이메일
    private String buyerTel; // 구매자 전화번호
    private String buyerAddr; // 구매자 주소
    private String buyerPostcode; // 구매자 우편번호
}