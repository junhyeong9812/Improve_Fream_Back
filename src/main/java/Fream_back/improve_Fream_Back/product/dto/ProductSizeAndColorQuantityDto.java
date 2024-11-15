package Fream_back.improve_Fream_Back.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSizeAndColorQuantityDto {
    private Long id;
    private String sizeType;
    private Set<String> clothingSizes; // 여러 의류 사이즈를 받을 수 있는 집합
    private Set<String> shoeSizes; // 여러 신발 사이즈를 받을 수 있는 집합
    private Set<String> colors;
    private int quantity;
}