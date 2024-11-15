package Fream_back.improve_Fream_Back.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSizeAndColorQuantityDto {
    private Long id;
    private String sizeType;
    private String clothingSize;
    private String shoeSize;
    private String color;
    private int quantity;
}