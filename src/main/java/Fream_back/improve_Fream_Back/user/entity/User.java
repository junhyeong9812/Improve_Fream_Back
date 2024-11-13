package Fream_back.improve_Fream_Back.user.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseEntity;
import Fream_back.improve_Fream_Back.product.entity.UserProduct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

/**
 * User
 *
 * 사용자 정보를 관리하는 엔티티입니다.
 * 사용자명, 비밀번호, 역할(Role), 연락 정보 및 동의 여부를 포함합니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID (기본 키)

    private String loginId; // 커뮤니티 아이디
    private String password; // 비밀번호
    private String nickname; // 커뮤니티에서 사용할 별명

    private String realName; // 유저의 본명
    private String phoneNumber; // 연락처 전화번호
    private String email; // 이메일

    private Boolean phoneNotificationConsent; // 전화 알림 수신 동의 여부
    private Boolean emailNotificationConsent; // 이메일 수신 동의 여부

    @Enumerated(EnumType.STRING)
    private Role role; // USER, ADMIN 등으로 역할 구분

    @OneToMany(mappedBy = "seller")
    private List<UserProduct> productsForSale; // 판매자로서 등록한 상품들

    public User(String loginId, String password, String nickname, String realName, String phoneNumber,
                String email, Boolean phoneNotificationConsent, Boolean emailNotificationConsent, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.realName = realName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.phoneNotificationConsent = phoneNotificationConsent;
        this.emailNotificationConsent = emailNotificationConsent;
        this.role = role;
    }

    // 연관관계 편의 메서드 - UserProduct 추가
    public void addProductForSale(UserProduct userProduct) {
        this.productsForSale.add(userProduct);
        userProduct.assignSeller(this);
    }
    // 비밀번호 업데이트 메서드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
