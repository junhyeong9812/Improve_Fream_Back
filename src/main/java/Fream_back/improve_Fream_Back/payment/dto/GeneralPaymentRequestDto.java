package Fream_back.improve_Fream_Back.payment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneralPaymentRequestDto extends PaymentRequestDto {
    private String impUid; // PortOne 고유 ID
    private String pgProvider; // PG사 이름
    private String receiptUrl; // 영수증 URL
    private String buyerName; // 구매자 이름
    private String buyerEmail; // 구매자 이메일
}
