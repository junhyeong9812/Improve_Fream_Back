package Fream_back.improve_Fream_Back.order.dto;

import Fream_back.improve_Fream_Back.payment.dto.PaymentRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstantOrderRequestDto {

    private Long saleBidId;                 // 판매 입찰 ID
    private Long addressId;                 // 주소 ID
    private boolean warehouseStorage;       // 창고 보관 여부
    private PaymentRequestDto paymentRequest; // 결제 정보
 }
