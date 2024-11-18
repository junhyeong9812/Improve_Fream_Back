package Fream_back.improve_Fream_Back.Category.dto;

import Fream_back.improve_Fream_Back.Category.entity.MainCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainCategoryDto {
    private Long id;
    private String name;

    public MainCategoryDto(MainCategory mainCategory) {
        this.id = mainCategory.getId();
        this.name = mainCategory.getName();
    }
}
