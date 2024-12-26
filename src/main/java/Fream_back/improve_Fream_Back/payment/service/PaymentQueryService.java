package Fream_back.improve_Fream_Back.payment.service;

import Fream_back.improve_Fream_Back.payment.dto.AccountPaymentDto;
import Fream_back.improve_Fream_Back.payment.dto.CardPaymentDto;
import Fream_back.improve_Fream_Back.payment.dto.GeneralPaymentDto;
import Fream_back.improve_Fream_Back.payment.dto.PaymentDto;
import Fream_back.improve_Fream_Back.payment.entity.AccountPayment;
import Fream_back.improve_Fream_Back.payment.entity.CardPayment;
import Fream_back.improve_Fream_Back.payment.entity.GeneralPayment;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentDto getPaymentDetails(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment instanceof GeneralPayment generalPayment) {
            return GeneralPaymentDto.builder()
                    .id(generalPayment.getId())
                    .paidAmount(generalPayment.getPaidAmount())
                    .paymentType("GENERAL")
                    .impUid(generalPayment.getImpUid())
                    .status(generalPayment.getStatus())
                    .paymentDate(generalPayment.getPaymentDate())
                    .pgProvider(generalPayment.getPgProvider())
                    .receiptUrl(generalPayment.getReceiptUrl())
                    .buyerName(generalPayment.getBuyerName())
                    .buyerEmail(generalPayment.getBuyerEmail())
                    .build();
        } else if (payment instanceof CardPayment cardPayment) {
            return CardPaymentDto.builder()
                    .id(cardPayment.getId())
                    .paidAmount(cardPayment.getPaidAmount())
                    .paymentType("CARD")
                    .impUid(cardPayment.getImpUid())
                    .status(cardPayment.getStatus())
                    .paymentDate(cardPayment.getPaymentDate())
                    .cardType(cardPayment.getCardType())
                    .receiptUrl(cardPayment.getReceiptUrl())
                    .pgProvider(cardPayment.getPgProvider())
                    .pgTid(cardPayment.getPgTid())
                    .build();
        } else if (payment instanceof AccountPayment accountPayment) {
            return AccountPaymentDto.builder()
                    .id(accountPayment.getId())
                    .paidAmount(accountPayment.getPaidAmount())
                    .paymentType("ACCOUNT")
                    .impUid(accountPayment.getImpUid())
                    .status(accountPayment.getStatus())
                    .paymentDate(accountPayment.getPaymentDate())
                    .bankName(accountPayment.getBankName())
                    .accountNumber(accountPayment.getAccountNumber())
                    .accountHolder(accountPayment.getAccountHolder())
                    .receiptRequested(accountPayment.isReceiptRequested())
                    .build();
        } else {
            throw new IllegalArgumentException("알 수 없는 결제 타입입니다.");
        }
    }
}

