package Fream_back.improve_Fream_Back.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SaleBidResponseDto {
    private Long saleBidId;
    private Long productId;
    private String productName;
    private String productEnglishName;
    private String size;
    private String colorName;
    private String thumbnailImageUrl;
    private int bidPrice;
    private String saleBidStatus;
    private String saleStatus;
    private String shipmentStatus;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
