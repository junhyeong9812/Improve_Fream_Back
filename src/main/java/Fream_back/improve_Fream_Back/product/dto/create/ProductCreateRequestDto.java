package Fream_back.improve_Fream_Back.product.dto.create;

import Fream_back.improve_Fream_Back.product.dto.ProductImageDto;
import Fream_back.improve_Fream_Back.product.dto.ProductSizeAndColorQuantityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDto {
    private String name;
    private String brand;
//    private String sku;
    private Long mainCategoryId;
    private Long subCategoryId;
    private BigDecimal initialPrice;
    private String description;
    private LocalDate releaseDate;
    private List<ProductImageDto> images;
    private Set<ProductSizeAndColorQuantityDto> sizeAndColorQuantities;
}
