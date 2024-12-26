package Fream_back.improve_Fream_Back.payment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.payment.dto.paymentInfo.PaymentInfoCreateDto;
import Fream_back.improve_Fream_Back.payment.entity.*;
import Fream_back.improve_Fream_Back.payment.service.paymentInfo.PaymentInfoQueryService;
import Fream_back.improve_Fream_Back.payment.service.paymentInfo.PortOneApiClient;
import Fream_back.improve_Fream_Back.sale.entity.Sale;
import Fream_back.improve_Fream_Back.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentInfoQueryService paymentInfoQueryService;
    private final PortOneApiClient portOneApiClient;

    //주문 기반 GeneralPayment 생성 및 상태 설정
    public String createGeneralPayment(Order order, String impUid, String pgProvider, String receiptUrl,
                                       String buyerName, String buyerEmail, double paidAmount) {
        try {
            // 1. GeneralPayment 생성 및 저장
            GeneralPayment payment = GeneralPayment.builder()
                    .impUid(impUid)
                    .pgProvider(pgProvider)
                    .receiptUrl(receiptUrl)
                    .buyerName(buyerName)
                    .buyerEmail(buyerEmail)
                    .paidAmount(paidAmount)
                    .build();
            payment.assignOrder(order);
            payment.updateStatus(PaymentStatus.PAID); // 결제 완료로 설정
            payment.updateSuccessStatus(true); // 성공 여부 설정

            paymentRepository.save(payment);

            // 2. 즉시 취소 요청
            boolean refundSuccess = portOneApiClient.cancelPayment(payment.getImpUid());

            if (refundSuccess) {
                payment.updateStatus(PaymentStatus.REFUNDED); // 상태를 환불 완료로 변경
                paymentRepository.save(payment);
                return "결제가 성공적으로 완료되었으며, 즉시 취소되었습니다. ImpUid: " + payment.getImpUid();
            } else {
                return "결제가 성공했으나 취소 요청에 실패했습니다. 관리자에게 문의하세요.";
            }

        } catch (Exception e) {
            return "결제 요청 중 오류가 발생하였습니다: " + e.getMessage();
        }
    }


    //주문 기반 카드 결제 생성 및 처리
    public String processOrderCardPayment(Order order, String userEmail, Long paymentInfoId, double paidAmount) {
        // 1. PaymentInfo 조회
        PaymentInfo paymentInfo = paymentInfoQueryService.getPaymentInfoEntity(userEmail, paymentInfoId);

        // 2. 결제 요청 생성
        try {
            // 포트원 API 요청
            Map<String, Object> response = portOneApiClient.processCardPayment(paymentInfo, paidAmount);

            // 3. 응답 데이터 저장
            CardPayment cardPayment = CardPayment.builder()
                    .cardNumber(paymentInfo.getCardNumber())
                    .cardPassword(paymentInfo.getCardPassword())
                    .cardExpiration(paymentInfo.getExpirationDate())
                    .birthDate(paymentInfo.getBirthDate())
                    .cardType((String) response.get("card_name")) // 카드사 이름
                    .paidAmount(paidAmount)
                    .impUid((String) response.get("imp_uid"))
                    .receiptUrl((String) response.get("receipt_url"))
                    .pgProvider((String) response.get("pg_provider"))
                    .pgTid((String) response.get("pg_tid"))
                    .build();

            cardPayment.assignOrder(order);
// 상태를 API 응답에 따라 설정
            if ("success".equals(response.get("status"))) {
                cardPayment.updateStatus(PaymentStatus.PAID);
                cardPayment.updateSuccessStatus(true); // 결제 성공으로 설정
            }
            paymentRepository.save(cardPayment);

            //return "결제가 성공적으로 완료되었습니다. ImpUid: " + cardPayment.getImpUid();

            // 4. 결제 성공 후 즉시 취소 요청
            boolean refundSuccess = portOneApiClient.cancelPayment(cardPayment.getImpUid());

            if (refundSuccess) {
                cardPayment.updateStatus(PaymentStatus.REFUNDED); // 상태를 환불 완료로 변경
                paymentRepository.save(cardPayment);
                return "결제가 성공적으로 완료되었으며, 즉시 취소되었습니다. ImpUid: " + cardPayment.getImpUid();
            } else {
                return "결제가 성공했으나 취소 요청에 실패했습니다. 관리자에게 문의하세요.";
            }


        } catch (Exception e) {
            // 실패 처리
            return "결제 요청에 실패하였습니다: " + e.getMessage();
        }
    }

    //판매 기반 카드 결제 생성 및 처리
    public String processSaleCardPayment(Sale sale, String userEmail, Long paymentInfoId, double paidAmount) {
        // 1. PaymentInfo 조회
        PaymentInfo paymentInfo = paymentInfoQueryService.getPaymentInfoEntity(userEmail, paymentInfoId);

        // 2. 포트원 API 요청 생성
        try {
            // 포트원 API 요청
            Map<String, Object> response = portOneApiClient.processCardPayment(paymentInfo, paidAmount);

            // 3. 응답 데이터 저장
            CardPayment cardPayment = CardPayment.builder()
                    .cardNumber(paymentInfo.getCardNumber())
                    .cardPassword(paymentInfo.getCardPassword())
                    .cardExpiration(paymentInfo.getExpirationDate())
                    .birthDate(paymentInfo.getBirthDate())
                    .cardType((String) response.get("card_name")) // 카드사 이름
                    .paidAmount(paidAmount)
                    .impUid((String) response.get("imp_uid"))
                    .receiptUrl((String) response.get("receipt_url"))
                    .pgProvider((String) response.get("pg_provider"))
                    .pgTid((String) response.get("pg_tid"))
                    .build();

            cardPayment.assignSale(sale);

            // 상태를 API 응답에 따라 설정
            if ("success".equals(response.get("status"))) {
                cardPayment.updateStatus(PaymentStatus.PAID);
                cardPayment.updateSuccessStatus(true); // 결제 성공으로 설정
            }
            // 결제 성공
            paymentRepository.save(cardPayment);

//            return "결제가 성공적으로 완료되었습니다. ImpUid: " + cardPayment.getImpUid();

            // 4. 결제 성공 후 즉시 취소 요청
            boolean refundSuccess = portOneApiClient.cancelPayment(cardPayment.getImpUid());

            if (refundSuccess) {
                cardPayment.updateStatus(PaymentStatus.REFUNDED); // 상태를 환불 완료로 변경
                paymentRepository.save(cardPayment);
                return "결제가 성공적으로 완료되었으며, 즉시 취소되었습니다. ImpUid: " + cardPayment.getImpUid();
            } else {
                return "결제가 성공했으나 취소 요청에 실패했습니다. 관리자에게 문의하세요.";
            }


        } catch (Exception e) {
            // 실패 처리
            return "결제 요청에 실패하였습니다: " + e.getMessage();
        }
    }

    //주문 기반 AccountPayment 생성
    public AccountPayment createAccountPayment(Order order, String bankName, String accountNumber,
                                               String accountHolder, boolean receiptRequested, double paidAmount) {
        validateBank(bankName); // 유효한 은행인지 확인
        AccountPayment payment = AccountPayment.builder()
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .receiptRequested(receiptRequested)
                .paidAmount(paidAmount)
                .build();
        payment.assignOrder(order);
        payment.updateStatus(PaymentStatus.PENDING); // 초기 상태: 결제 대기
        return paymentRepository.save(payment);
    }

    //은행 유효성 검사
    private void validateBank(String bankName) {
        try {
            Bank.valueOf(bankName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("유효하지 않은 은행 이름입니다: " + bankName);
        }
    }

    //결제 상태 변경
    public void updatePaymentStatus(Payment payment, PaymentStatus newStatus) {
        payment.updateStatus(newStatus);
        paymentRepository.save(payment);
    }

    public String refundPayment(Long paymentId) {
        // 1. Payment 정보 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 2. 현재 상태 확인
        if (!payment.getStatus().canTransitionTo(PaymentStatus.REFUND_REQUESTED)) {
            throw new IllegalStateException("현재 상태에서 환불 요청을 진행할 수 없습니다.");
        }

        // 3. 포트원 API를 통해 환불 요청
        try {
            boolean refundSuccess = portOneApiClient.cancelPayment(payment.getImpUid());

            if (refundSuccess) {
                payment.updateStatus(PaymentStatus.REFUNDED); // 상태를 REFUNDED로 변경
                paymentRepository.save(payment);
                return "환불이 성공적으로 완료되었습니다.";
            } else {
                payment.updateStatus(PaymentStatus.REFUND_REQUESTED); // 환불 요청 상태로 업데이트
                paymentRepository.save(payment);
                return "환불 요청이 실패하였습니다. 관리자에게 문의하세요.";
            }
        } catch (Exception e) {
            throw new RuntimeException("환불 요청 중 오류가 발생하였습니다: " + e.getMessage());
        }
    }
}
