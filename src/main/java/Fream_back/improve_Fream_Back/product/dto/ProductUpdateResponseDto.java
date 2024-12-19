package Fream_back.improve_Fream_Back.product.dto;

import Fream_back.improve_Fream_Back.product.entity.enumType.GenderType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateResponseDto {
    private Long id;
    private String name;
    private String englishName;
    private int releasePrice;
    private String modelNumber;
    private String releaseDate;
    private String brandName;
    private String categoryName;
    private String collectionName;
    private GenderType gender; // 상품 성별 추가


}
