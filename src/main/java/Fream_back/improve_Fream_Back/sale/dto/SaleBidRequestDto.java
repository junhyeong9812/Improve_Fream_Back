package Fream_back.improve_Fream_Back.sale.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleBidRequestDto {
    private Long productSizeId; // 상품 사이즈 ID
    private int bidPrice; // 입찰 가격
    private String returnAddress; // 반송 주소
    private String postalCode; // 우편번호
    private String receiverPhone; // 수령인 전화번호
    private boolean warehouseStorage; // 창고 보관 여부
}
