package Fream_back.improve_Fream_Back.product.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    private String mainCategoryName; // 메인 카테고리명
    private String subCategoryName; // 서브 카테고리명 (선택)
}
