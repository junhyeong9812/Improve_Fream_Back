package Fream_back.improve_Fream_Back.payment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountPaymentRequestDto extends PaymentRequestDto {
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private boolean receiptRequested; // 현금영수증 요청 여부
}

