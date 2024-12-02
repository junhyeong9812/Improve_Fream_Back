package Fream_back.improve_Fream_Back.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {
    private String recipientName; // 수령인 이름
    private String phoneNumber; // 연락처
    private String address; // 배송지 주소
    private String addressDetail; // 상세 주소
    private String zipCode; // 우편번호
}
