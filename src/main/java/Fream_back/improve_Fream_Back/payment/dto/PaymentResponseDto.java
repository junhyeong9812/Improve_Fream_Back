package Fream_back.improve_Fream_Back.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PaymentResponseDto {
    private Long paymentId;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private boolean isSuccessful;
}
