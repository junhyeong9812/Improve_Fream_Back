package Fream_back.improve_Fream_Back.Category.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubCategoryRequestDto {
    private Long mainCategoryId; // 상위 카테고리 ID
    private String subCategoryName; // 하위 카테고리 이름
}
