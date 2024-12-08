package Fream_back.improve_Fream_Back.payment.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결제 ID

//    @ManyToOne(fetch = FetchType.LAZY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 연결된 주문

    // PortOne 관련 필드
    @Column(name = "apply_num", nullable = false)
    private String applyNum; // 승인 번호

    @Column(name = "bank_name")
    private String bankName; // 은행 이름 (계좌이체 등)

    @Column(name = "buyer_addr", nullable = false)
    private String buyerAddr; // 구매자 주소

    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail; // 구매자 이메일

    @Column(name = "buyer_name", nullable = false)
    private String buyerName; // 구매자 이름

    @Column(name = "buyer_postcode", nullable = false)
    private String buyerPostcode; // 구매자 우편번호

    @Column(name = "buyer_tel", nullable = false)
    private String buyerTel; // 구매자 전화번호

    @Column(name = "card_name", nullable = false)
    private String cardName; // 카드사 이름

    @Column(name = "card_number", nullable = false)
    private String cardNumber; // 카드 번호

    @Column(name = "card_quota", nullable = false)
    private int cardQuota; // 할부 개월 수

    @Column(name = "currency", nullable = false)
    private String currency; // 통화 (e.g., KRW)

    @Column(name = "custom_data")
    private String customData; // 사용자 정의 데이터

    @Column(name = "imp_uid", nullable = false)
    private String impUid; // PortOne 고유 ID

    @Column(name = "merchant_uid", nullable = false)
    private String merchantUid; // 상점 주문 고유 ID

    @Column(name = "product_name", nullable = false)
    private String productName; // 상품명

    @Column(name = "paid_amount", nullable = false)
    private double paidAmount; // 결제 금액

    @Column(name = "paid_at", nullable = false)
    private long paidAt; // 결제 완료 시간 (Unix timestamp)

    @Column(name = "pay_method", nullable = false)
    private String payMethod; // 결제 수단

    @Column(name = "pg_provider", nullable = false)
    private String pgProvider; // PG사 이름

    @Column(name = "pg_tid", nullable = false)
    private String pgTid; // PG사 거래 ID

    @Column(name = "pg_type", nullable = false)
    private String pgType; // PG사 타입

    @Column(name = "receipt_url", nullable = false)
    private String receiptUrl; // 영수증 URL

    @Column(name = "status", nullable = false)
    private String status; // 결제 상태 (e.g., "paid")

    @Builder.Default
    @Column(name = "success", nullable = false)
    private boolean success = false; // 결제 성공 여부

    @Builder.Default
    @Column(name = "is_refunded", nullable = false)
    private boolean isRefunded = false; // 환불 여부 (기본값: false)

//    @Column(name = "cancelled_at")
//    private Long cancelledAt; // 환불 완료 시간 (Unix timestamp)

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt; // 환불 완료 시간 (LocalDateTime)


    // 연관관계 편의 메서드
    public void assignOrder(Order order) {
        this.order = order;
    }
    public void updatePaymentDetails(
            String impUid,
            String merchantUid,
            String payMethod,
            double paidAmount,
            String buyerName,
            String buyerEmail,
            String buyerTel,
            String buyerAddr,
            String buyerPostcode,
            boolean success
    ) {
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.payMethod = payMethod;
        this.paidAmount = paidAmount;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerTel = buyerTel;
        this.buyerAddr = buyerAddr;
        this.buyerPostcode = buyerPostcode;
        this.success = success;
    }
    //환불 업데이트 메서드
    public void updateRefundDetails(boolean isRefunded, long cancelledAtUnix) {
        if (!this.isSuccess()) {
            throw new IllegalStateException("결제가 성공하지 않은 상태에서는 환불을 처리할 수 없습니다.");
        }
        if (this.isRefunded()) {
            throw new IllegalStateException("이미 환불된 결제입니다.");
        }

        // UNIX timestamp를 LocalDateTime으로 변환
        LocalDateTime cancelledAt = LocalDateTime.ofEpochSecond(cancelledAtUnix, 0, ZoneOffset.UTC);

        this.isRefunded = isRefunded;
        this.cancelledAt = cancelledAt;
    }
}