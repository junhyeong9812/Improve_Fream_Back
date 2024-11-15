package Fream_back.improve_Fream_Back.delivery.dto;


import Fream_back.improve_Fream_Back.delivery.entity.Delivery;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class DeliveryDto {

    private Long id; // 배송 ID
    private String recipientName; // 수령인 이름
    private String phoneNumber; // 수령인 전화번호
    private String address; // 배송지 주소
    private String addressDetail; // 상세 주소
    private String zipCode; // 우편번호
    private boolean isDefault; // 기본 배송지 여부

    // 필드들을 초기화하는 생성자 추가
    public DeliveryDto(String recipientName, String phoneNumber, String address, String addressDetail, String zipCode, boolean isDefault) {
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
        this.isDefault = isDefault;
    }
    // 엔티티를 DTO로 변환하는 메서드
    public static DeliveryDto fromEntity(Delivery delivery) {
        return new DeliveryDto(
                delivery.getId(), // 배송 ID 포함
                delivery.getRecipientName(),
                delivery.getPhoneNumber(),
                delivery.getAddress(),
                delivery.getAddressDetail(),
                delivery.getZipCode(),
                delivery.isDefault()
        );
    }

//    // 정렬을 위한 우선순위 필드
//    public static int compareByIsDefault(DeliveryDto d1, DeliveryDto d2) {
//        return Boolean.compare(d2.isDefault(), d1.isDefault());
//    }
    //sorted / Boolean.compare을 통해 getDeliveries 메소드에 진행
}
