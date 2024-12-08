package Fream_back.improve_Fream_Back.payment.service;

import Fream_back.improve_Fream_Back.order.entity.Order;
import Fream_back.improve_Fream_Back.order.repository.OrderRepository;
import Fream_back.improve_Fream_Back.payment.dto.*;
import Fream_back.improve_Fream_Back.payment.entity.Payment;
import Fream_back.improve_Fream_Back.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * 결제 생성
     */
    public Payment createPayment(Order order) {
        Payment payment = Payment.builder()
                .order(order)               // Order와 연관 설정
                .success(false)             // 초기 성공 여부는 false
                .status("pending")          // 초기 상태는 "pending"
                .applyNum("")               // 승인 번호 초기화
                .impUid("")                 // PortOne 고유 ID 초기화
                .merchantUid(order.getId().toString()) // Order ID를 기반으로 상점 주문 고유 ID 설정
                .pgProvider("")             // PG사 이름 초기화
                .pgTid("")                  // PG사 거래 ID 초기화
                .buyerName(order.getRecipientName())   // 주문 수령인 정보 사용
                .buyerEmail(order.getPhoneNumber())    // 주문 전화번호 사용
                .currency("KRW")            // 기본 통화
                .paidAt(0L)                 // 결제 완료 시간 초기화
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    /**
     * 결제 성공 처리
     */
    @Transactional
    public PaymentResponseDto updatePaymentDetails(Long paymentId, PaymentRequestDto requestDto) {
        // 1. 결제 정보 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 2. 결제 정보 업데이트
        payment.updatePaymentDetails(
                requestDto.getImpUid(),
                requestDto.getMerchantUid(),
                requestDto.getPayMethod(),
                requestDto.getPaidAmount(),
                requestDto.getBuyerName(),
                requestDto.getBuyerEmail(),
                requestDto.getBuyerTel(),
                requestDto.getBuyerAddr(),
                requestDto.getBuyerPostcode(),
                true // 결제 성공 여부
        );

        // 3. 반환값 생성
        return new PaymentResponseDto(payment.getId(), payment.isSuccess());
    }

    /**
     * 결제 취소 (환불 처리)
     */
    @Transactional
    public void refundPayment(Long paymentId, PaymentRefundRequestDto refundRequestDto) {
        // 1. Payment 정보 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (!payment.isSuccess()) {
            throw new IllegalStateException("결제에 성공하지 않은 경우 환불할 수 없습니다.");
        }

        // 2. PortOne API 요청 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("imp_uid", payment.getImpUid());
        requestBody.put("merchant_uid", payment.getMerchantUid());
        requestBody.put("amount", refundRequestDto.getAmount());
        requestBody.put("reason", refundRequestDto.getReason());
        requestBody.put("refund_holder", refundRequestDto.getRefundHolder());
        requestBody.put("refund_bank", refundRequestDto.getRefundBank());
        requestBody.put("refund_account", refundRequestDto.getRefundAccount());

        // 3. PortOne API 호출
        RestTemplate restTemplate = new RestTemplate();
        String portOneUrl = "https://api.iamport.kr/payments/cancel";
        ResponseEntity<PortOneRefundResponseDto> response = restTemplate.postForEntity(portOneUrl, requestBody, PortOneRefundResponseDto.class);

        // 4. API 호출 결과 처리
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || !response.getBody().isSuccess()) {
            throw new IllegalStateException("PortOne 환불 요청이 실패했습니다: " + response.getBody().getMessage());
        }

        // 5. Payment 상태 업데이트
        payment.updateRefundDetails(true, response.getBody().getResponse().getCancelledAt());
        paymentRepository.save(payment);

        // 6. Order 상태 업데이트
        Order order = payment.getOrder();
        if (order != null) {
            order.markPaymentCompleted(false); // 주문 상태 초기화
        }
    }
    //즉시 환불
    @Transactional
    public void refundPayment(Long paymentId) {
        // 1. Payment 정보 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 2. 환불 가능 여부 확인
        if (!payment.isSuccess()) {
            throw new IllegalStateException("결제가 성공하지 않은 상태에서는 환불을 처리할 수 없습니다.");
        }

        if (payment.isRefunded()) {
            throw new IllegalStateException("이미 환불된 결제입니다.");
        }

        // 3. PortOne API 요청 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("imp_uid", payment.getImpUid());
        requestBody.put("merchant_uid", payment.getMerchantUid());
        requestBody.put("amount", payment.getPaidAmount()); // 전액 환불
        requestBody.put("reason", "사용자 요청에 의한 환불"); // 기본 환불 사유

        // 4. PortOne API 호출
        RestTemplate restTemplate = new RestTemplate();
        String portOneUrl = "https://api.iamport.kr/payments/cancel";
        ResponseEntity<PortOneRefundResponseDto> response = restTemplate.postForEntity(portOneUrl, requestBody, PortOneRefundResponseDto.class);

        // 5. API 호출 결과 처리
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || !response.getBody().isSuccess()) {
            throw new IllegalStateException("PortOne 환불 요청이 실패했습니다: " + response.getBody().getMessage());
        }

        // 6. Payment 상태 업데이트
        payment.updateRefundDetails(true, response.getBody().getResponse().getCancelledAt());
        paymentRepository.save(payment);

        // 7. Order 상태 업데이트
        Order order = payment.getOrder();
        if (order != null) {
            order.markPaymentCompleted(false); // 주문 상태 초기화
        }
    }

    //결제 조회
    @Transactional(readOnly = true)
    public PaymentDetailsDto getPaymentDetailsByOrder(Long orderId) {
        // Order에서 Payment 조회
        Payment payment = paymentRepository.findSinglePaymentByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // PaymentDetailsDto로 변환
        return PaymentDetailsDto.builder()
                .paymentId(payment.getId())
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .payMethod(payment.getPayMethod())
                .paidAmount(payment.getPaidAmount())
                .isSuccessful(payment.isSuccess())
                .isRefunded(payment.isRefunded())
                .buyerName(payment.getBuyerName())
                .buyerEmail(payment.getBuyerEmail())
                .buyerTel(payment.getBuyerTel())
                .buyerAddr(payment.getBuyerAddr())
                .buyerPostcode(payment.getBuyerPostcode())
                .paidAt(payment.getPaidAt())
                .status(payment.getStatus())
                .build();
    }
}
