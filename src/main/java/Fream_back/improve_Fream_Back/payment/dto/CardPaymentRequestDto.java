package Fream_back.improve_Fream_Back.payment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardPaymentRequestDto extends PaymentRequestDto {
    private Long paymentInfoId;

}
