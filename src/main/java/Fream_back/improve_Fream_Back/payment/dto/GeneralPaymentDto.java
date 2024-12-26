package Fream_back.improve_Fream_Back.payment.dto;

import Fream_back.improve_Fream_Back.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GeneralPaymentDto implements PaymentDto {
    private Long id;
    private double paidAmount;
    private String paymentType;
    private String impUid;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    private String pgProvider;
    private String receiptUrl;
    private String buyerName;
    private String buyerEmail;
}