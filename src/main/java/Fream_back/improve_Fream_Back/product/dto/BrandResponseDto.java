package Fream_back.improve_Fream_Back.product.dto;

import Fream_back.improve_Fream_Back.product.entity.Brand;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponseDto {
    private Long id; // 브랜드 ID
    private String name; // 브랜드명

    public static BrandResponseDto fromEntity(Brand brand) {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }
}
