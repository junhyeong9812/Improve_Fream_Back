package Fream_back.improve_Fream_Back.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private boolean isSuccessful;

}
