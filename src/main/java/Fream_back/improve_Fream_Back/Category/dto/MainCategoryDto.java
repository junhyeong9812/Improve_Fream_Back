package Fream_back.improve_Fream_Back.Category.dto;

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
}
