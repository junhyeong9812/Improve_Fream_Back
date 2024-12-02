package Fream_back.improve_Fream_Back.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    private String paymentMethod; // 결제 수단
    private BigDecimal amount; // 결제 금액
}