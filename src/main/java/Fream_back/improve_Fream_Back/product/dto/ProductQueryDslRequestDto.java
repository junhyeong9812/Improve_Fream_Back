package Fream_back.improve_Fream_Back.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQueryDslRequestDto {
    private Long mainCategoryId;
    private Long subCategoryId;
    private String color; //색상
    private String size; //사이즈
    private String brand; // 브랜드
    private String sortBy; // 정렬 방식
}