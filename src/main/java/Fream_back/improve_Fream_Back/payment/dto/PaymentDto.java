package Fream_back.improve_Fream_Back.payment.dto;

import Fream_back.improve_Fream_Back.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public interface PaymentDto {
    Long getId();
    double getPaidAmount();
    String getPaymentType();
    String getImpUid();
    PaymentStatus getStatus();
    LocalDateTime getPaymentDate();
}
