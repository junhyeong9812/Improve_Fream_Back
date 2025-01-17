package Fream_back.improve_Fream_Back.payment.dto;

import Fream_back.improve_Fream_Back.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountPaymentDto implements PaymentDto {
    private Long id;
    private double paidAmount;
    private String paymentType;
    private String impUid;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private boolean receiptRequested;
}
