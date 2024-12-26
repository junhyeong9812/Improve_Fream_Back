package Fream_back.improve_Fream_Back.payment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("CARD")
public class CardPayment extends Payment {

    // 결제 당시의 카드 정보
    private String cardNumber;       // 카드 번호 (마스킹 처리된 번호)
    private String cardExpiration;  // 카드 유효기간
    private String cardHolderName;  // 카드 소유자 이름
    private String cardType;        // 카드 타입 (e.g., Visa, MasterCard)

    @Builder
    public CardPayment(String cardNumber, String cardExpiration, String cardHolderName, String cardType, double paidAmount) {
        this.cardNumber = cardNumber;
        this.cardExpiration = cardExpiration;
        this.cardHolderName = cardHolderName;
        this.cardType = cardType;
        this.setPaidAmount(paidAmount);
    }
}
