package Fream_back.improve_Fream_Back.product.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponseDto {
    private Long id;
    private String name;
    private String englishName;
    private int releasePrice;
    private String thumbnailImageUrl; // 대표 이미지 URL
    private Integer price; // 가장 낮은 구매가 추가
    private String colorName; // 해당 이미지의 색상명 추가
}

