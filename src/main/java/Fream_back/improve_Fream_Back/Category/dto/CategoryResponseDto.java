package Fream_back.improve_Fream_Back.Category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class CategoryResponseDto {
    private Long mainCategoryId; // 상위 카테고리 ID
    private String mainCategoryName; // 상위 카테고리 이름
    private Set<String> subCategoryNames; // 하위 카테고리 이름 리스트
}