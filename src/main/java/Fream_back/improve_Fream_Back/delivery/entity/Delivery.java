package Fream_back.improve_Fream_Back.delivery.entity;

import Fream_back.improve_Fream_Back.base.entity.BaseTimeEntity;
import Fream_back.improve_Fream_Back.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Delivery
 *
 * 배송 정보를 관리하는 엔티티입니다.
 * 여러 배송지 정보를 저장할 수 있으며 사용자와 연관됩니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Delivery extends BaseTimeEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 배송 ID (기본 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 배송 정보를 가지는 사용자

    private String recipientName; // 수령인 이름
    private String phoneNumber; // 수령인 전화번호
    private String address; // 배송지 주소
    private String addressDetail; // 상세 주소
    private String zipCode; // 우편번호

    // 배송지 기본 설정 여부
    private boolean isDefault; // 기본 배송지 여부

    // 연관관계 편의 메서드 - User 지정
    public void assignUser(User user) {
        this.user = user;
    }

    // 기본 배송지 여부 설정 메서드
    public void setAsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    // 배송지 정보 업데이트 메서드
    public void updateDelivery(String recipientName, String phoneNumber, String address, String addressDetail, String zipCode, boolean isDefault) {
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
        this.isDefault = isDefault;
    }
}


