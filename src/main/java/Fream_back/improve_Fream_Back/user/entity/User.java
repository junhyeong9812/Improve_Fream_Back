package Fream_back.improve_Fream_Back.user.entity;

import Fream_back.improve_Fream_Back.address.entity.Address;
import Fream_back.improve_Fream_Back.base.entity.BaseEntity;
import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.payment.entity.PaymentInfo;
import Fream_back.improve_Fream_Back.product.entity.Interest;
import Fream_back.improve_Fream_Back.product.entity.UserProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email; // 이메일 주소

    @Column(nullable = false)
    private String password; // 비밀번호

    private String referralCode; // 추천인 코드

    @Enumerated(EnumType.STRING)
    private ShoeSize shoeSize; // 신발 사이즈 (Enum)

    private boolean termsAgreement; // 이용약관 동의 여부

    private Boolean phoneNotificationConsent; // 전화 알림 수신 동의 여부
    private Boolean emailNotificationConsent; // 이메일 수신 동의 여부

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN 등으로 역할 구분

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile; // 프로필 (1:1 관계)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interest> interests = new ArrayList<>(); // 관심 상품 (다대다 중간 테이블)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>(); // 주소록

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentInfo> paymentInfos = new ArrayList<>(); // 결제 정보

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private BankAccount bankAccount; // 판매 정산 계좌 (1:1 관계)

    // **편의 메서드 - 값 업데이트**
    public void updateUser(String email, String password, ShoeSize shoeSize, Boolean phoneNotificationConsent, Boolean emailNotificationConsent) {
        if (email != null) {
            this.email = email;
        }
        if (password != null) {
            this.password = password;
        }
        if (shoeSize != null) {
            this.shoeSize = shoeSize;
        }
        if (phoneNotificationConsent != null) {
            this.phoneNotificationConsent = phoneNotificationConsent;
        }
        if (emailNotificationConsent != null) {
            this.emailNotificationConsent = emailNotificationConsent;
        }
    }
    // 편의 메서드
    public void assignBankAccount(BankAccount bankAccount) {
        if (this.bankAccount != null) {
            this.bankAccount.unassignUser();
        }
        this.bankAccount = bankAccount;
        if (bankAccount != null) {
            bankAccount.assignUser(this);
        }
    }

    public void removeBankAccount() {
        if (this.bankAccount != null) {
            this.bankAccount.unassignUser();
            this.bankAccount = null;
        }
    }

    public void addPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfos.add(paymentInfo);
        paymentInfo.assignUser(this);
    }

    public void removePaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfos.remove(paymentInfo);
        paymentInfo.unassignUser();
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
        address.assignUser(this);
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
        address.unassignUser();
    }

    public void addInterest(Interest interest) {
        this.interests.add(interest);
        interest.assignUser(this);
    }

    public void removeInterest(Interest interest) {
        this.interests.remove(interest);
        interest.unassignUser();
    }

}






