package Fream_back.improve_Fream_Back.product.dto;

import Fream_back.improve_Fream_Back.Category.dto.MainCategoryDto;
import Fream_back.improve_Fream_Back.Category.dto.SubCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private String brand;
    private String sku;
    private MainCategoryDto mainCategory;
    private SubCategoryDto subCategory;
    private BigDecimal initialPrice;
    private String description;
    private List<ProductImageDto> images;
    private Set<ProductSizeAndColorQuantityDto> sizeAndColorQuantities;
}