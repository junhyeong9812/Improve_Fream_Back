package Fream_back.improve_Fream_Back.Category.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CategoryRequestDto {
    private String mainCategoryName; // 상위 카테고리 이름
    private Set<String> subCategoryNames; // 하위 카테고리 이름 리스트
}
