package Fream_back.improve_Fream_Back.product.dto;

import Fream_back.improve_Fream_Back.product.entity.Category;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {
    private Long id; // 카테고리 ID
    private String name; // 카테고리명
    private Long parentCategoryId; // 상위 카테고리 ID (없을 경우 null)

    public static CategoryResponseDto fromEntity(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .build();
    }
}