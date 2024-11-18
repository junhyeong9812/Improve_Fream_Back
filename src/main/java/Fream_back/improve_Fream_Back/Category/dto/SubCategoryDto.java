package Fream_back.improve_Fream_Back.Category.dto;

import Fream_back.improve_Fream_Back.Category.entity.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryDto {
    private Long id;
    private String name;
    private MainCategoryDto mainCategory;

    public SubCategoryDto(SubCategory subCategory) {
        this.id = subCategory.getId();
        this.name = subCategory.getName();
        this.mainCategory = new MainCategoryDto(subCategory.getMainCategory());
    }
}