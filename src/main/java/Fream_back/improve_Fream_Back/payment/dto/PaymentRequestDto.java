package Fream_back.improve_Fream_Back.payment.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "paymentType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CardPaymentRequestDto.class, name = "CARD"),
        @JsonSubTypes.Type(value = AccountPaymentRequestDto.class, name = "ACCOUNT"),
        @JsonSubTypes.Type(value = GeneralPaymentRequestDto.class, name = "GENERAL")
})
public class PaymentRequestDto {
    private String paymentType; // CARD, ACCOUNT, GENERAL
    private double paidAmount;
    private Long orderId; // 주문 ID
    private String userEmail; // 사용자 이메일
}
