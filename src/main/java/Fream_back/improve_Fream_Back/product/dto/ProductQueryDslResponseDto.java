package Fream_back.improve_Fream_Back.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductQueryDslResponseDto {
    private Long id;
    private String name;
    private String brand;
    private String mainCategoryName;
    private String subCategoryName;
    private String color;
    private String size;
    private int quantity;
}